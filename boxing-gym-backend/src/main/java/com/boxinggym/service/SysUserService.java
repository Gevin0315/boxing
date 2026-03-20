package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.SysUserQueryDTO;
import com.boxinggym.entity.SysUser;
import com.boxinggym.vo.SysUserVO;

/**
 * 系统用户 Service
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询系统用户
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<SysUserVO> page(SysUserQueryDTO query);
}
