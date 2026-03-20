package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.SysUserQueryDTO;
import com.boxinggym.entity.SysUser;
import com.boxinggym.enums.RoleEnum;
import com.boxinggym.mapper.SysUserMapper;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.SysUserVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户 Service 实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public Page<SysUserVO> page(SysUserQueryDTO query) {
        Page<SysUser> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (query.getUsername() != null && !query.getUsername().isEmpty()) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        // 真实姓名模糊查询
        if (query.getRealName() != null && !query.getRealName().isEmpty()) {
            wrapper.like(SysUser::getRealName, query.getRealName());
        }
        // 手机号模糊查询
        if (query.getPhone() != null && !query.getPhone().isEmpty()) {
            wrapper.like(SysUser::getPhone, query.getPhone());
        }
        // 状态精确查询
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        // 角色精确查询
        if (query.getRole() != null && !query.getRole().isEmpty()) {
            wrapper.eq(SysUser::getRole, query.getRole());
        }

        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = page(page, wrapper);

        // 转换为 VO
        Page<SysUserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<SysUserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     */
    private SysUserVO convertToVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setRoleDescription(getRoleDescription(user.getRole()));
        vo.setStatus(user.getStatus());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    /**
     * 获取角色描述
     */
    private String getRoleDescription(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            return "";
        }
        try {
            RoleEnum roleEnum = RoleEnum.fromCode(roleCode);
            return roleEnum.getDescription();
        } catch (IllegalArgumentException e) {
            return roleCode;
        }
    }
}
