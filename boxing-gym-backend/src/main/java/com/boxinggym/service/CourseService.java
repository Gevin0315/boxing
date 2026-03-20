package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.vo.CourseVO;

/**
 * 课程 Service
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<CourseVO> page(CourseQueryDTO query);
}
