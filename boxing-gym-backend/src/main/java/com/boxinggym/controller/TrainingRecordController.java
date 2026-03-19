package com.boxinggym.controller;

import com.boxinggym.common.BusinessException;
import com.boxinggym.common.Result;
import com.boxinggym.dto.BatchDeleteDTO;
import com.boxinggym.dto.CheckinWithCardDTO;
import com.boxinggym.dto.MemberCardVO;
import com.boxinggym.entity.Course;
import com.boxinggym.entity.Member;
import com.boxinggym.enums.CourseTypeEnum;
import com.boxinggym.service.MemberCardService;
import com.boxinggym.utils.SecurityUtil;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.service.CourseService;
import com.boxinggym.service.TrainingRecordService;
import com.boxinggym.service.MemberService;
import com.boxinggym.service.SysUserService;
import com.boxinggym.utils.ResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.boxinggym.common.Result.*;

/**
 * 上课签到记录 Controller
 */
@Tag(name = "上课签到管理", description = "上课签到记录相关接口")
@RestController
@RequestMapping("/api/training-record")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION','ROLE_COACH')")
public class TrainingRecordController {

    private final TrainingRecordService trainingRecordService;
    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final MemberService memberService;
    private final SysUserService sysUserService;
    private final MemberCardService memberCardService;

    // 字典映射
    private static final Map<Integer, String> STATUS_LABEL = Map.of(
            0, "已预约",
            1, "已签到",
            2, "已取消",
            3, "旷课"
    );
    private static final Map<Integer, String> COURSE_TYPE_LABEL = Map.of(
            1, "团课",
            2, "私教课"
    );
    private static final Map<Integer, String> COURSE_LEVEL_LABEL = Map.of(
            1, "初级",
            2, "中级",
            3, "高级"
    );
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 查询所有签到记录
     */
    @Operation(summary = "查询所有签到记录")
    @GetMapping("/list")
    public Result<List<TrainingRecord>> list() {
        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .orderByDesc(TrainingRecord::getCreateTime)
                .list();

        // 填充会员名和课程名
        list.forEach(record -> {
            // 填充会员名
            Member member = memberService.getById(record.getMemberId());
            if (member != null) {
                record.setMemberName(member.getName());
            }
            // 填充课程名
            CourseSchedule schedule = courseScheduleService.getById(record.getScheduleId());
            if (schedule != null) {
                Course course = courseService.getById(schedule.getCourseId());
                if (course != null) {
                    record.setCourseName(course.getName());
                }
            }
        });

        return Result.success(list);
    }

    /**
     * 根据ID查询签到记录
     */
    @Operation(summary = "根据ID查询签到记录")
    @GetMapping("/{id}")
    public Result<TrainingRecord> getById(@PathVariable Long id) {
        TrainingRecord record = trainingRecordService.getById(id);
        if (record == null) {
            return notFound("签到记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 根据会员ID查询签到记录
     */
    @Operation(summary = "根据会员ID查询签到记录")
    @GetMapping("/member/{memberId}")
    public Result<List<TrainingRecord>> getByMemberId(@PathVariable Long memberId) {
        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getMemberId, memberId)
                .orderByDesc(TrainingRecord::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 根据教练ID查询签到记录
     */
    @Operation(summary = "根据教练ID查询签到记录")
    @GetMapping("/coach/{coachId}")
    public Result<List<TrainingRecord>> getByCoachId(@PathVariable Long coachId) {
        if (SecurityUtil.isCoach() && !coachId.equals(SecurityUtil.getCurrentUserId())) {
            throw new BusinessException(403, "教练只能查询自己的签到记录");
        }
        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getCoachId, coachId)
                .orderByDesc(TrainingRecord::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 新增签到记录
     */
    @Operation(summary = "新增签到记录")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> add(@RequestBody TrainingRecord record) {
        boolean success = trainingRecordService.save(record);
        return success ? success("新增成功") : fail("新增失败");
    }

    /**
     * 修改签到记录
     */
    @Operation(summary = "修改签到记录")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> update(@RequestBody TrainingRecord record) {
        boolean success = trainingRecordService.updateById(record);
        return success ? success("修改成功") : fail("修改失败");
    }

    /**
     * 删除签到记录
     */
    @Operation(summary = "删除签到记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = trainingRecordService.removeById(id);
        return success ? success("删除成功") : fail("删除失败");
    }

    /**
     * 批量删除签到记录
     */
    @Operation(summary = "批量删除签到记录")
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> batchDelete(@Valid @RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return fail("ID列表不能为空");
        }
        boolean success = trainingRecordService.removeByIds(dto.getIds());
        return success ? success("批量删除成功") : fail("批量删除失败");
    }

    /**
     * 签到
     */
    @Operation(summary = "会员签到")
    @Transactional
    @PostMapping("/check-in")
    public Result<String> checkIn(@RequestParam Long scheduleId, @RequestParam Long memberId) {
        CourseSchedule schedule = courseScheduleService.getById(scheduleId);
        if (schedule == null) {
            return notFound("排课不存在");
        }

        long count = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getScheduleId, scheduleId)
                .eq(TrainingRecord::getMemberId, memberId)
                .ne(TrainingRecord::getStatus, 2)
                .count();

        if (count > 0) {
            return fail("该会员已签到或已预约此课程");
        }

        TrainingRecord record = new TrainingRecord();
        record.setRecordNo("REC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        record.setScheduleId(scheduleId);
        record.setMemberId(memberId);
        record.setCoachId(schedule.getCoachId());
        record.setCheckinTime(LocalDateTime.now());
        record.setStatus(1);
        boolean success = trainingRecordService.save(record);
        return success ? success("签到成功") : fail("签到失败");
    }

    /**
     * 使用会员卡签到
     */
    @Operation(summary = "使用会员卡签到")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/check-in-with-card")
    public Result<Map<String, Object>> checkInWithCard(@Valid @RequestBody CheckinWithCardDTO dto) {
        CourseSchedule schedule = courseScheduleService.getById(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排课不存在");
        }

        // 检查是否已签到或预约
        long count = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getScheduleId, dto.getScheduleId())
                .eq(TrainingRecord::getMemberId, dto.getMemberId())
                .ne(TrainingRecord::getStatus, 2)
                .count();

        if (count > 0) {
            throw new BusinessException("该会员已签到或已预约此课程");
        }

        // 获取课程信息，判断是否为私教课
        Course course = courseService.getById(schedule.getCourseId());
        boolean isPrivateClass = course != null && CourseTypeEnum.PRIVATE.getCode().equals(course.getType());

        Long memberCardId = dto.getMemberCardId();

        // 如果未指定卡片，自动选择最佳卡片
        if (memberCardId == null) {
            if (isPrivateClass) {
                List<MemberCardVO> privateCards = memberCardService.getAvailablePrivateCards(dto.getMemberId());
                if (privateCards.isEmpty()) {
                    throw new BusinessException("没有可用的私教卡");
                }
                memberCardId = privateCards.get(0).getId();
            } else {
                memberCardId = memberCardService.selectBestGroupCard(dto.getMemberId());
                if (memberCardId == null) {
                    throw new BusinessException("没有可用的团课卡");
                }
            }
        } else {
            // 验证卡片是否可用
            if (!memberCardService.validateCardForCheckin(memberCardId, dto.getMemberId(), isPrivateClass)) {
                throw new BusinessException("该会员卡不可用于此次签到");
            }
        }

        // 创建签到记录
        TrainingRecord record = new TrainingRecord();
        record.setRecordNo("REC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        record.setScheduleId(dto.getScheduleId());
        record.setMemberId(dto.getMemberId());
        record.setCoachId(schedule.getCoachId());
        record.setCheckinTime(LocalDateTime.now());
        record.setStatus(1);
        record.setMemberCardId(memberCardId);
        record.setRemark(dto.getRemark());
        trainingRecordService.save(record);

        // 扣减卡片次数
        memberCardService.deductSession(memberCardId, dto.getScheduleId(), record.getId());

        // 更新卡片类型信息
        MemberCardVO cardVO = memberCardService.getDetail(memberCardId);
        record.setCardTypeUsed(cardVO.getCardType());

        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("cardNo", cardVO.getCardNo());
        result.put("cardType", cardVO.getCardTypeDesc());
        result.put("remainingSessions", cardVO.getRemainingSessions());

        return Result.success(result);
    }

    /**
     * 签退
     */
    @Operation(summary = "会员签退")
    @PutMapping("/check-out")
    public Result<String> checkOut(@RequestParam Long id) {
        TrainingRecord record = trainingRecordService.getById(id);
        if (record == null) {
            return notFound("签到记录不存在");
        }

        if (record.getStatus() != 1) {
            return fail("当前状态无法签退");
        }

        boolean success = trainingRecordService.lambdaUpdate()
                .eq(TrainingRecord::getId, id)
                .set(TrainingRecord::getStatus, 2)
                .update();

        return success ? success("签退成功") : fail("签退失败");
    }

    /**
     * 获取排课的签到记录
     */
    @Operation(summary = "获取排课的签到记录")
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<TrainingRecord>> getByScheduleId(@PathVariable Long scheduleId) {
        CourseSchedule schedule = courseScheduleService.getById(scheduleId);
        if (schedule == null) {
            return notFound("排课不存在");
        }

        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getScheduleId, scheduleId)
                .ne(TrainingRecord::getStatus, 2)
                .orderByAsc(TrainingRecord::getCheckinTime)
                .list();

        return Result.success(list);
    }
}
