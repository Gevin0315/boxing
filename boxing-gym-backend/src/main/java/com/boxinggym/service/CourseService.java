package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.vo.CourseVO;

import java.util.List;

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

    /**
     * 更新课程状态
     * <p>
     * 当课程状态更新为禁用时，会自动取消该课程下所有未开始的排课记录。
     * </p>
     *
     * @param id     课程ID
     * @param status 状态（0-禁用，1-启用）
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除课程
     * <p>
     * 删除课程时会级联取消所有未结束的排课记录。
     * </p>
     *
     * @param id 课程ID
     */
    void deleteById(Long id);

    /**
     * 批量删除课程
     * <p>
     * 批量删除课程时会级联取消每个课程下所有未结束的排课记录。
     * </p>
     *
     * @param ids 课程ID列表
     */
    void deleteByIds(List<Long> ids);
}
