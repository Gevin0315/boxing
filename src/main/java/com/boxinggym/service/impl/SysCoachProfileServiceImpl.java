package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.SysCoachProfile;
import com.boxinggym.mapper.SysCoachProfileMapper;
import com.boxinggym.service.SysCoachProfileService;
import org.springframework.stereotype.Service;

/**
 * 教练扩展信息 Service 实现
 */
@Service
public class SysCoachProfileServiceImpl extends ServiceImpl<SysCoachProfileMapper, SysCoachProfile> implements SysCoachProfileService {
}
