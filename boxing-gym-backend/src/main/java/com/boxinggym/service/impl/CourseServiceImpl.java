package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.CourseQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.SysUser;
import com.boxinggym.mapper.CourseMapper;
import com.boxinggym.service.CourseService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程 Service 实现
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final SysUserService sysUserService;

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
}
