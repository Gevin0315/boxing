package com.boxinggym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxinggym.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排课记录 Mapper
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
}
