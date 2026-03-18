package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.CardUsageRecordVO;
import com.boxinggym.entity.CardUsageRecord;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.Member;
import com.boxinggym.entity.MemberCard;
import com.boxinggym.entity.SysUser;
import com.boxinggym.enums.CardUsageTypeEnum;
import com.boxinggym.mapper.CardUsageRecordMapper;
import com.boxinggym.mapper.CourseScheduleMapper;
import com.boxinggym.mapper.MemberCardMapper;
import com.boxinggym.mapper.MemberMapper;
import com.boxinggym.mapper.SysUserMapper;
import com.boxinggym.service.CardUsageRecordService;
import com.boxinggym.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员卡使用记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardUsageRecordServiceImpl extends ServiceImpl<CardUsageRecordMapper, CardUsageRecord> implements CardUsageRecordService {

    private final MemberCardMapper memberCardMapper;
    private final MemberMapper memberMapper;
    private final SysUserMapper sysUserMapper;
    private final CourseScheduleMapper courseScheduleMapper;

    private static final DateTimeFormatter RECORD_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public void recordUsage(Long memberCardId, Long memberId, Integer usageType,
                            Long scheduleId, Long trainingRecordId,
                            Integer sessionsBefore, Integer sessionsAfter, String remark) {
        CardUsageRecord record = new CardUsageRecord();
        record.setRecordNo("CU" + LocalDateTime.now().format(RECORD_NO_FORMATTER));
        record.setMemberCardId(memberCardId);
        record.setMemberId(memberId);
        record.setUsageType(usageType);
        record.setScheduleId(scheduleId);
        record.setTrainingRecordId(trainingRecordId);
        record.setSessionsBefore(sessionsBefore);
        record.setSessionsAfter(sessionsAfter);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        try {
            record.setCreateBy(SecurityUtil.getCurrentUserId());
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
        }
        save(record);
    }

    @Override
    public List<CardUsageRecordVO> getCardUsageRecords(Long memberCardId) {
        LambdaQueryWrapper<CardUsageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardUsageRecord::getMemberCardId, memberCardId)
                .orderByDesc(CardUsageRecord::getCreateTime);
        List<CardUsageRecord> records = list(wrapper);
        return convertToVOList(records);
    }

    @Override
    public Page<CardUsageRecordVO> page(Page<?> page, Long memberCardId, Long memberId) {
        LambdaQueryWrapper<CardUsageRecord> wrapper = new LambdaQueryWrapper<>();
        if (memberCardId != null) {
            wrapper.eq(CardUsageRecord::getMemberCardId, memberCardId);
        }
        if (memberId != null) {
            wrapper.eq(CardUsageRecord::getMemberId, memberId);
        }
        wrapper.orderByDesc(CardUsageRecord::getCreateTime);
        Page<CardUsageRecord> recordPage = new Page<>(page.getCurrent(), page.getSize());
        Page<CardUsageRecord> result = page(recordPage, wrapper);
        Page<CardUsageRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(convertToVOList(result.getRecords()));
        return voPage;
    }

    /**
     * 批量转换为VO
     */
    private List<CardUsageRecordVO> convertToVOList(List<CardUsageRecord> records) {
        if (records.isEmpty()) {
            return List.of();
        }
        // 批量获取关联数据
        List<Long> memberCardIds = records.stream().map(CardUsageRecord::getMemberCardId).distinct().toList();
        List<Long> memberIds = records.stream().map(CardUsageRecord::getMemberId).distinct().toList();
        List<Long> scheduleIds = records.stream().map(CardUsageRecord::getScheduleId).filter(id -> id != null).distinct().toList();
        List<Long> createBys = records.stream().map(CardUsageRecord::getCreateBy).filter(id -> id != null).distinct().toList();
        // 查询关联数据
        Map<Long, MemberCard> cardMap = memberCardIds.isEmpty() ? Map.of() :
                memberCardMapper.selectBatchIds(memberCardIds).stream()
                        .collect(Collectors.toMap(MemberCard::getId, c -> c));
        Map<Long, Member> memberMap = memberIds.isEmpty() ? Map.of() :
                memberMapper.selectBatchIds(memberIds).stream()
                        .collect(Collectors.toMap(Member::getId, m -> m));
        Map<Long, CourseSchedule> scheduleMap = scheduleIds.isEmpty() ? Map.of() :
                courseScheduleMapper.selectBatchIds(scheduleIds).stream()
                        .collect(Collectors.toMap(CourseSchedule::getId, s -> s));
        Map<Long, SysUser> userMap = createBys.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(createBys).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));
        return records.stream().map(record -> {
            CardUsageRecordVO vo = new CardUsageRecordVO();
            BeanUtils.copyProperties(record, vo);
            // 设置卡号
            MemberCard card = cardMap.get(record.getMemberCardId());
            if (card != null) {
                vo.setCardNo(card.getCardNo());
            }
            // 设置会员名称
            Member member = memberMap.get(record.getMemberId());
            if (member != null) {
                vo.setMemberName(member.getName());
            }
            // 设置使用类型描述
            try {
                CardUsageTypeEnum type = CardUsageTypeEnum.fromCode(record.getUsageType());
                vo.setUsageTypeDesc(type.getDescription());
            } catch (IllegalArgumentException e) {
                vo.setUsageTypeDesc("未知");
            }
            // 设置课程名称
            if (record.getScheduleId() != null) {
                CourseSchedule schedule = scheduleMap.get(record.getScheduleId());
                if (schedule != null) {
                    vo.setCourseName("排课ID:" + schedule.getId());
                }
            }
            // 设置操作人名称
            if (record.getCreateBy() != null) {
                SysUser user = userMap.get(record.getCreateBy());
                if (user != null) {
                    vo.setOperatorName(user.getRealName());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
