package com.boxinggym.controller;

import com.boxinggym.common.BusinessException;
import com.boxinggym.common.Result;
import com.boxinggym.entity.CourseSchedule;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.service.CourseScheduleService;
import com.boxinggym.service.TrainingRecordService;
import com.boxinggym.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    /**
     * 查询所有签到记录
     */
    @Operation(summary = "查询所有签到记录")
    @GetMapping("/list")
    public Result<List<TrainingRecord>> list() {
        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .orderByDesc(TrainingRecord::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 根据ID查询签到记录
     */
    @Operation(summary = "根据ID查询签到记录")
    @GetMapping("/{id}")
    public Result<TrainingRecord> getById(@PathVariable Long id) {
        TrainingRecord record = trainingRecordService.getById(id);
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
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 修改签到记录
     */
    @Operation(summary = "修改签到记录")
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> update(@RequestBody TrainingRecord record) {
        boolean success = trainingRecordService.updateById(record);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除签到记录
     */
    @Operation(summary = "删除签到记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = trainingRecordService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
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
            return Result.notFound("排课不存在");
        }

        // 检查是否已签到
        long count = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getScheduleId, scheduleId)
                .eq(TrainingRecord::getMemberId, memberId)
                .ne(TrainingRecord::getStatus, 2) // 排除已取消的
                .count();

        if (count > 0) {
            return Result.fail("该会员已签到或已预约此课程");
        }

        // 创建签到记录
        TrainingRecord record = new TrainingRecord();
        record.setRecordNo("REC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        record.setScheduleId(scheduleId);
        record.setMemberId(memberId);
        record.setCoachId(schedule.getCoachId());
        record.setCheckinTime(LocalDateTime.now());
        record.setStatus(1); // 已签到
        boolean success = trainingRecordService.save(record);

        return success ? Result.success("签到成功") : Result.fail("签到失败");
    }

    /**
     * 签退
     */
    @Operation(summary = "会员签退")
    @PutMapping("/check-out")
    public Result<String> checkOut(@RequestParam Long id) {
        TrainingRecord record = trainingRecordService.getById(id);
        if (record == null) {
            return Result.notFound("签到记录不存在");
        }

        if (record.getStatus() != 1) {
            return Result.fail("当前状态无法签退");
        }

        // 标记为已完成（这里用status=2表示已完成，实际可能需要根据业务调整）
        boolean success = trainingRecordService.lambdaUpdate()
                .eq(TrainingRecord::getId, id)
                .set(TrainingRecord::getStatus, 2)
                .update();

        return success ? Result.success("签退成功") : Result.fail("签退失败");
    }

    /**
     * 获取排课的签到记录
     */
    @Operation(summary = "获取排课的签到记录")
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<TrainingRecord>> getByScheduleId(@PathVariable Long scheduleId) {
        List<TrainingRecord> list = trainingRecordService.lambdaQuery()
                .eq(TrainingRecord::getScheduleId, scheduleId)
                .ne(TrainingRecord::getStatus, 2) // 排除已取消的
                .orderByAsc(TrainingRecord::getCheckinTime)
                .list();
        return Result.success(list);
    }
}
