package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.mapper.CourseScheduleMapper;
import com.boxinggym.service.CourseScheduleService;
import org.springframework.stereotype.Service;

/**
 * 排课记录 Service 实现
 */
@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> implements CourseScheduleService {
}
