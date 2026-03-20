package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.TrainingRecordQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.Member;
import com.boxinggym.entity.SysUser;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.mapper.TrainingRecordMapper;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.service.CourseService;
import com.boxinggym.service.MemberService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.service.TrainingRecordService;
import com.boxinggym.vo.TrainingRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 上课签到记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class TrainingRecordServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements TrainingRecordService {

    private final MemberService memberService;
    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final SysUserService sysUserService;

    /**
     * 分页查询签到记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public Page<TrainingRecordVO> page(TrainingRecordQueryDTO query) {
        Page<TrainingRecord> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<TrainingRecord> wrapper = new LambdaQueryWrapper<>();

        // 会员ID精确查询
        if (query.getMemberId() != null) {
            wrapper.eq(TrainingRecord::getMemberId, query.getMemberId());
        }
        // 排课ID精确查询
        if (query.getScheduleId() != null) {
            wrapper.eq(TrainingRecord::getScheduleId, query.getScheduleId());
        }
        // 状态精确查询
        if (query.getStatus() != null) {
            wrapper.eq(TrainingRecord::getStatus, query.getStatus());
        }

        // 按创建时间降序排列
        wrapper.orderByDesc(TrainingRecord::getCreateTime);
        Page<TrainingRecord> result = page(page, wrapper);

        // 批量查询关联数据，避免 N+1 问题
        Set<Long> memberIds = result.getRecords().stream()
                .map(TrainingRecord::getMemberId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> scheduleIds = result.getRecords().stream()
                .map(TrainingRecord::getScheduleId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> coachIds = result.getRecords().stream()
                .map(TrainingRecord::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 批量查询会员信息
        Map<Long, Member> memberMap = Map.of();
        if (!memberIds.isEmpty()) {
            List<Member> members = memberService.listByIds(memberIds);
            memberMap = members.stream()
                    .collect(Collectors.toMap(Member::getId, m -> m, (a, b) -> a));
        }

        // 批量查询排课信息
        Map<Long, CourseSchedule> scheduleMap = Map.of();
        Set<Long> courseIds = Set.of();
        if (!scheduleIds.isEmpty()) {
            List<CourseSchedule> schedules = courseScheduleService.listByIds(scheduleIds);
            scheduleMap = schedules.stream()
                    .collect(Collectors.toMap(CourseSchedule::getId, s -> s, (a, b) -> a));
            courseIds = schedules.stream()
                    .map(CourseSchedule::getCourseId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }

        // 批量查询课程信息
        Map<Long, Course> courseMap = Map.of();
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseService.listByIds(courseIds);
            courseMap = courses.stream()
                    .collect(Collectors.toMap(Course::getId, c -> c, (a, b) -> a));
        }

        // 批量查询教练信息
        Map<Long, SysUser> coachMap = Map.of();
        if (!coachIds.isEmpty()) {
            List<SysUser> coaches = sysUserService.listByIds(coachIds);
            coachMap = coaches.stream()
                    .collect(Collectors.toMap(SysUser::getId, c -> c, (a, b) -> a));
        }

        // 转换为 VO
        final Map<Long, Member> finalMemberMap = memberMap;
        final Map<Long, CourseSchedule> finalScheduleMap = scheduleMap;
        final Map<Long, Course> finalCourseMap = courseMap;
        final Map<Long, SysUser> finalCoachMap = coachMap;

        List<TrainingRecordVO> voList = result.getRecords().stream()
                .map(record -> convertToVO(record, finalMemberMap, finalScheduleMap, finalCourseMap, finalCoachMap))
                .collect(Collectors.toList());

        Page<TrainingRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     *
     * @param record      签到记录实体
     * @param memberMap   会员信息映射
     * @param scheduleMap 排课信息映射
     * @param courseMap   课程信息映射
     * @param coachMap    教练信息映射
     * @return 签到记录 VO
     */
    private TrainingRecordVO convertToVO(TrainingRecord record,
                                         Map<Long, Member> memberMap,
                                         Map<Long, CourseSchedule> scheduleMap,
                                         Map<Long, Course> courseMap,
                                         Map<Long, SysUser> coachMap) {
        TrainingRecordVO vo = new TrainingRecordVO();
        vo.setId(record.getId());
        vo.setRecordNo(record.getRecordNo());
        vo.setScheduleId(record.getScheduleId());
        vo.setCoachId(record.getCoachId());
        vo.setMemberId(record.getMemberId());
        vo.setCheckinTime(record.getCheckinTime());
        vo.setCheckoutTime(record.getCheckoutTime());
        vo.setStatus(record.getStatus());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());

        // 填充会员信息
        Member member = memberMap.get(record.getMemberId());
        if (member != null) {
            vo.setMemberName(member.getName());
        }

        // 填充排课和课程信息
        CourseSchedule schedule = scheduleMap.get(record.getScheduleId());
        if (schedule != null) {
            vo.setCourseId(schedule.getCourseId());
            Course course = courseMap.get(schedule.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
            }
        }

        // 填充教练信息
        SysUser coach = coachMap.get(record.getCoachId());
        if (coach != null) {
            vo.setCoachName(coach.getRealName());
        }

        return vo;
    }
}
