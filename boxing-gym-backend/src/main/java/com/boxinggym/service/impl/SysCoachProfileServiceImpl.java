package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.CoachQueryDTO;
import com.boxinggym.entity.SysCoachProfile;
import com.boxinggym.entity.SysUser;
import com.boxinggym.mapper.SysCoachProfileMapper;
import com.boxinggym.service.SysCoachProfileService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.CoachVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 教练扩展信息 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysCoachProfileServiceImpl extends ServiceImpl<SysCoachProfileMapper, SysCoachProfile> implements SysCoachProfileService {

    private final SysUserService sysUserService;

    /**
     * 分页查询教练
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public Page<CoachVO> page(CoachQueryDTO query) {
        // 先根据条件筛选出符合条件的用户ID
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getRole, "ROLE_COACH");

        // 姓名模糊查询
        if (query.getName() != null && !query.getName().isEmpty()) {
            userWrapper.like(SysUser::getRealName, query.getName());
        }
        // 状态精确查询
        if (query.getStatus() != null) {
            userWrapper.eq(SysUser::getStatus, query.getStatus());
        }

        List<SysUser> filteredUsers = sysUserService.list(userWrapper);

        // 如果没有符合条件的用户，返回空结果
        if (filteredUsers.isEmpty()) {
            Page<CoachVO> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 获取符合条件的用户ID
        Set<Long> userIds = filteredUsers.stream()
                .map(SysUser::getId)
                .collect(Collectors.toSet());

        // 构建用户ID到用户信息的映射
        Map<Long, SysUser> userMap = filteredUsers.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        // 分页查询教练档案（只查询符合条件的用户）
        Page<SysCoachProfile> profilePage = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysCoachProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysCoachProfile::getUserId, userIds);
        wrapper.orderByDesc(SysCoachProfile::getCreateTime);

        Page<SysCoachProfile> result = page(profilePage, wrapper);

        // 转换为 VO
        List<CoachVO> voList = result.getRecords().stream()
                .map(profile -> convertToVO(profile, userMap))
                .collect(Collectors.toList());

        Page<CoachVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     *
     * @param profile 教练档案实体
     * @param userMap 用户信息映射
     * @return 教练 VO
     */
    private CoachVO convertToVO(SysCoachProfile profile, Map<Long, SysUser> userMap) {
        CoachVO vo = new CoachVO();
        vo.setId(profile.getId());
        vo.setUserId(profile.getUserId());
        vo.setCoachNo(profile.getUserId() != null ? String.format("C%06d", profile.getUserId()) : "");
        vo.setGender(profile.getGender());
        vo.setPhone(profile.getPhone());
        vo.setEmail(profile.getEmail());
        vo.setSpecialties(profile.getSpecialty());
        vo.setLevel(profile.getLevel());
        vo.setImageUrl(profile.getImgUrl());
        vo.setDescription(profile.getIntro());
        vo.setHireDate(profile.getHireDate());
        vo.setCreateTime(profile.getCreateTime());
        vo.setUpdateTime(profile.getUpdateTime());

        // 从用户表获取姓名和状态
        SysUser user = userMap.get(profile.getUserId());
        if (user != null) {
            vo.setName(user.getRealName());
            vo.setStatus(user.getStatus());
        }
        return vo;
    }
}
