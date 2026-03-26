package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.SysUser;
import com.boxinggym.mapper.CourseMapper;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.service.CourseService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final SysUserService sysUserService;
    private final CourseScheduleService courseScheduleService;

    @Override
    public Page<CourseVO> page(CourseQueryDTO query) {
        Page<Course> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        // 课程名称模糊查询
        if (query.getName() != null && !query.getName().isEmpty()) {
            wrapper.like(Course::getName, query.getName());
        }
        // 课程类型精确查询
        if (query.getType() != null) {
            wrapper.eq(Course::getType, query.getType());
        }
        // 课程分类精确查询
        if (query.getCategory() != null && !query.getCategory().isEmpty()) {
            wrapper.eq(Course::getCategory, query.getCategory());
        }
        // 课程难度精确查询
        if (query.getLevel() != null && !query.getLevel().isEmpty()) {
            wrapper.eq(Course::getLevel, query.getLevel());
        }
        // 教练ID精确查询
        if (query.getCoachId() != null) {
            wrapper.eq(Course::getCoachId, query.getCoachId());
        }
        // 状态精确查询
        if (query.getStatus() != null) {
            wrapper.eq(Course::getStatus, query.getStatus());
        }

        // 按排序字段升序排列
        wrapper.orderByAsc(Course::getSortOrder);
        Page<Course> result = page(page, wrapper);

        // 获取所有教练ID
        Set<Long> coachIds = result.getRecords().stream()
                .map(Course::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 批量查询教练名称
        Map<Long, String> coachNameMap = Map.of();
        if (!coachIds.isEmpty()) {
            List<SysUser> coaches = sysUserService.listByIds(coachIds);
            coachNameMap = coaches.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        }

        // 转换为 VO
        final Map<Long, String> finalCoachNameMap = coachNameMap;
        Page<CourseVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<CourseVO> voList = result.getRecords().stream()
                .map(course -> convertToVO(course, finalCoachNameMap))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     *
     * @param course       课程实体
     * @param coachNameMap 教练名称映射
     * @return 课程 VO
     */
    private CourseVO convertToVO(Course course, Map<Long, String> coachNameMap) {
        CourseVO vo = new CourseVO();
        vo.setId(course.getId());
        vo.setCourseName(course.getName());
        vo.setCourseType(course.getType());
        vo.setCategory(course.getCategory());
        vo.setLevel(course.getLevel());
        vo.setDuration(course.getDuration());
        vo.setMaxCapacity(course.getMaxCapacity());
        vo.setPrice(course.getPrice());
        vo.setCoachId(course.getCoachId());
        vo.setCoachName(course.getCoachId() != null ? coachNameMap.getOrDefault(course.getCoachId(), "") : "");
        vo.setImageUrl(course.getCoverImg());
        vo.setStatus(course.getStatus());
        vo.setCreateTime(course.getCreateTime());
        vo.setUpdateTime(course.getUpdateTime());
        return vo;
    }

    /**
     * 删除课程
     * <p>
     * 删除课程时会级联取消所有未结束的排课记录。
     * 使用事务确保数据一致性，删除失败时自动回滚。
     * </p>
     *
     * @param id 课程ID
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteById(Long id) {
        Course course = getById(id);
        if (course != null) {
            // 取消所有未结束的排课
            courseScheduleService.cancelUnfinishedSchedules(id);
            log.info("删除课程[{}]并取消关联排课", course.getName());
        }
        removeById(id);
    }

    /**
     * 批量删除课程
     * <p>
     * 批量删除课程时会级联取消每个课程下所有未结束的排课记录。
     * 使用事务确保数据一致性，任一删除失败时自动回滚。
     * </p>
     *
     * @param ids 课程ID列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        log.info("批量删除{}个课程", ids.size());
        for (Long id : ids) {
            deleteById(id);
        }
    }

    /**
     * 更新课程状态
     * <p>
     * 当课程状态更新为禁用时，会自动取消该课程下所有未开始的排课记录。
     * 使用事务确保数据一致性，更新失败时自动回滚。
     * </p>
     *
     * @param id     课程ID
     * @param status 状态（0-禁用，1-启用）
     * @throws IllegalArgumentException 当课程不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean updateStatus(Long id, Integer status) {
        Course course = getById(id);
        if (course == null) {
            return false;
        }
        course.setStatus(status);
        updateById(course);

        // 当禁用课程时，取消所有未开始的排课
        if (status == 0) {
            courseScheduleService.cancelNotStartedSchedules(id, "课程已禁用");
            log.info("禁用课程[{}]并取消未开始的排课", course.getName());
        }
        log.info("更新课程[{}]状态为: {}", course.getName(), status == 1 ? "启用" : "禁用");
        return true;
    }
}
