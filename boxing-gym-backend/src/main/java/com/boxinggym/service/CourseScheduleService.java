package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.CourseScheduleQueryDTO;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.vo.CourseScheduleVO;

/**
 * 排课记录 Service
 */
public interface CourseScheduleService extends IService<CourseSchedule> {

    /**
     * 分页查询排课
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<CourseScheduleVO> page(CourseScheduleQueryDTO query);
}
