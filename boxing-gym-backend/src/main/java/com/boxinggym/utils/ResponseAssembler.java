package com.boxinggym.utils;

import com.boxinggym.dto.FinanceOrderDTO;
import com.boxinggym.dto.TrainingRecordDTO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.entity.Member;
import com.boxinggym.entity.SysCoachProfile;
import com.boxinggym.entity.SysUser;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.service.MemberService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 响应数据组装工具类
 * 将实体转换为包含关联信息的 DTO
 */
public class ResponseAssembler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 组装财务订单 DTO
     */
    public static FinanceOrderDTO toFinanceOrderDTO(
            FinanceOrder order,
            Member member,
            Course course,
            Map<Long, String> courseTypeMap,
            Map<Integer, String> payMethodMap,
            Map<Integer, String> paymentStatusMap) {

        FinanceOrderDTO dto = new FinanceOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setType(order.getType());
        dto.setAmount(order.getAmount());
        dto.setPaidAmount(order.getPaidAmount() != null ? order.getPaidAmount() : order.getAmount());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());

        // 会员信息
        if (member != null) {
            dto.setMemberId(member.getId());
            dto.setMemberName(member.getName());
        }

        // 课程信息
        if (course != null) {
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getName());
            if (courseTypeMap != null) {
                String courseTypeLabel = courseTypeMap.get(course.getType());
                dto.setCourseType(courseTypeLabel != null ? courseTypeLabel : "");
            }
        }

        // 支付方式标签
        if (payMethodMap != null && order.getPayMethod() != null) {
            String payMethodLabel = payMethodMap.get(order.getPayMethod());
            dto.setPayMethodLabel(payMethodLabel != null ? payMethodLabel : "");
        }

        // 支付状态标签
        if (paymentStatusMap != null && order.getPaymentStatus() != null) {
            String paymentStatusLabel = paymentStatusMap.get(order.getPaymentStatus());
            dto.setPaymentStatusLabel(paymentStatusLabel != null ? paymentStatusLabel : "");
        }

        return dto;
    }

    /**
     * 组装训练记录 DTO
     */
    public static TrainingRecordDTO toTrainingRecordDTO(
            TrainingRecord record,
            Member member,
            SysUser coach,
            Course course,
            CourseSchedule schedule,
            Map<String, String> courseTypeMap,
            Map<Integer, String> courseLevelMap,
            Map<Integer, String> statusMap,
            Map<Long, SysUser> userCache,
            Map<Long, Course> courseCache) {

        TrainingRecordDTO dto = new TrainingRecordDTO();
        dto.setId(record.getId());
        dto.setRecordNo(record.getRecordNo());
        dto.setRemark(record.getRemark());
        dto.setCheckinTime(record.getCheckinTime());
        dto.setCheckoutTime(record.getCheckoutTime());
        dto.setStatus(record.getStatus());
        dto.setCreateTime(record.getCreateTime());
        dto.setUpdateTime(record.getUpdateTime());

        // 状态标签
        if (statusMap != null && record.getStatus() != null) {
            dto.setStatusLabel(statusMap.get(record.getStatus()));
        }

        // 会员信息
        if (member != null) {
            dto.setMemberId(member.getId());
            dto.setMemberName(member.getName());
        }

        // 教练信息
        if (coach != null) {
            dto.setCoachId(coach.getId());
            dto.setCoachName(coach.getRealName());
        }

        // 课程信息
        if (course != null) {
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getName());

            if (courseTypeMap != null) {
                String courseTypeLabel = courseTypeMap.get(course.getType());
                dto.setCourseType(courseTypeLabel != null ? courseTypeLabel : "");
            }

            if (courseLevelMap != null) {
                String courseLevelLabel = courseLevelMap.get(course.getLevel());
            }
        }

        // 排课信息
        if (schedule != null) {
            dto.setScheduleId(schedule.getId());
            dto.setClassroom(schedule.getClassroom());
        }

        return dto;
    }

    /**
     * 从缓存或查询获取用户
     */
    private static SysUser getUser(Long userId, Map<Long, SysUser> userCache) {
        if (userCache != null) {
            return userCache.get(userId);
        }
        return null;
    }

    /**
     * 从缓存或查询获取课程
     */
    private static Course getCourse(Long courseId, Map<Long, Course> courseCache) {
        if (courseCache != null) {
            return courseCache.get(courseId);
        }
        return null;
    }

    /**
     * 批量查询会员
     */
    public static Map<Long, Member> getMemberMap(List<Long> memberIds, MemberService memberService) {
        if (memberIds == null || memberIds.isEmpty()) {
            return new HashMap<>();
        }
        List<Member> members = memberService.listByIds(memberIds);
        return members.stream().collect(Collectors.toMap(Member::getId, m -> m));
    }

    /**
     * 批量查询课程
     */
    public static Map<Long, Course> getCourseMap(List<Long> courseIds, com.boxinggym.service.CourseService courseService) {
        if (courseIds == null || courseIds.isEmpty()) {
            return new HashMap<>();
        }
        List<Course> courses = courseService.listByIds(courseIds);
        return courses.stream().collect(Collectors.toMap(Course::getId, c -> c));
    }
}
