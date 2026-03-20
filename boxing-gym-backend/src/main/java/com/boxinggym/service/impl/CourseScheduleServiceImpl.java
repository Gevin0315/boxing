package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.CourseScheduleQueryDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.SysUser;
import com.boxinggym.mapper.CourseScheduleMapper;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.service.CourseService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.vo.CourseScheduleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排课记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> implements CourseScheduleService {

    private final CourseService courseService;
    private final SysUserService sysUserService;

    @Override
    public Page<CourseScheduleVO> page(CourseScheduleQueryDTO query) {
        Page<CourseSchedule> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();

        // 课程ID精确查询
        if (query.getCourseId() != null) {
            wrapper.eq(CourseSchedule::getCourseId, query.getCourseId());
        }
        // 教练ID精确查询
        if (query.getCoachId() != null) {
            wrapper.eq(CourseSchedule::getCoachId, query.getCoachId());
        }
        // 状态精确查询
        if (query.getStatus() != null) {
            wrapper.eq(CourseSchedule::getStatus, query.getStatus());
        }
        // 日期范围筛选
        if (query.getStartDate() != null && !query.getStartDate().isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(query.getStartDate()).atStartOfDay();
            wrapper.ge(CourseSchedule::getStartTime, startDateTime);
        }
        if (query.getEndDate() != null && !query.getEndDate().isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(query.getEndDate()).atTime(LocalTime.MAX);
            wrapper.le(CourseSchedule::getStartTime, endDateTime);
        }

        // 按开始时间升序排序
        wrapper.orderByAsc(CourseSchedule::getStartTime);
        Page<CourseSchedule> result = page(page, wrapper);

        // 获取所有课程ID和教练ID
        Set<Long> courseIds = result.getRecords().stream()
                .map(CourseSchedule::getCourseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Set<Long> coachIds = result.getRecords().stream()
                .map(CourseSchedule::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 批量查询课程名称
        Map<Long, Course> courseMap = Map.of();
        if (!courseIds.isEmpty()) {
            List<Course> courses = courseService.listByIds(courseIds);
            courseMap = courses.stream()
                    .collect(Collectors.toMap(Course::getId, c -> c, (a, b) -> a));
        }

        // 批量查询教练名称
        Map<Long, String> coachNameMap = Map.of();
        if (!coachIds.isEmpty()) {
            List<SysUser> coaches = sysUserService.listByIds(coachIds);
            coachNameMap = coaches.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        }

        // 转换为 VO
        final Map<Long, Course> finalCourseMap = courseMap;
        final Map<Long, String> finalCoachNameMap = coachNameMap;
        Page<CourseScheduleVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<CourseScheduleVO> voList = result.getRecords().stream()
                .map(schedule -> convertToVO(schedule, finalCourseMap, finalCoachNameMap))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     *
     * @param schedule     排课实体
     * @param courseMap    课程映射
     * @param coachNameMap 教练名称映射
     * @return 排课 VO
     */
    private CourseScheduleVO convertToVO(CourseSchedule schedule, Map<Long, Course> courseMap, Map<Long, String> coachNameMap) {
        CourseScheduleVO vo = new CourseScheduleVO();
        vo.setId(schedule.getId());
        vo.setCourseId(schedule.getCourseId());
        vo.setCoachId(schedule.getCoachId());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setClassroom(schedule.getClassroom());
        vo.setMaxCapacity(schedule.getMaxPeople());
        vo.setCurrentCount(schedule.getCurrentPeople());
        vo.setStatus(schedule.getStatus());
        vo.setRemark(schedule.getRemark());
        vo.setCreateTime(schedule.getCreateTime());

        // 填充课程名称和类型
        if (schedule.getCourseId() != null && courseMap.containsKey(schedule.getCourseId())) {
            Course course = courseMap.get(schedule.getCourseId());
            vo.setCourseName(course.getName());
            vo.setCourseType(course.getType());
        }

        // 填充教练名称
        if (schedule.getCoachId() != null) {
            vo.setCoachName(coachNameMap.getOrDefault(schedule.getCoachId(), ""));
        }

        return vo;
    }
}
