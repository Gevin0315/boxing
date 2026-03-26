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

    /**
     * 取消课程的所有未结束排课
     * <p>
     * 用于课程删除或禁用时，取消该课程下所有未结束的排课记录。
     * 未结束的排课包括：未开始、进行中的状态。
     * </p>
     *
     * @param courseId 课程ID
     */
    void cancelUnfinishedSchedules(Long courseId);

    /**
     * 取消课程的所有未开始排课
     * <p>
     * 用于课程删除或禁用时，取消该课程下所有未开始的排课记录。
     * 相比 {@link #cancelUnfinishedSchedules(Long)}，此方法只取消未开始的排课，
     * 不包括进行中的排课。支持记录取消原因。
     * </p>
     *
     * @param courseId 课程ID
     * @param reason 取消原因，用于记录和通知
     */
    void cancelNotStartedSchedules(Long courseId, String reason);
}
