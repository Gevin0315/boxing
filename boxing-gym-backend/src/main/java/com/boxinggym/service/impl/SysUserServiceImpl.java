package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.SysUser;
import com.boxinggym.mapper.SysUserMapper;
import com.boxinggym.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 系统用户 Service 实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
