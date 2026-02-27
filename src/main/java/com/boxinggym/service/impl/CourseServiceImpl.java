package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.Course;
import com.boxinggym.mapper.CourseMapper;
import com.boxinggym.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程 Service 实现
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}
