package com.boxinggym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.common.Result;
import com.boxinggym.dto.MembershipCardDTO;
import com.boxinggym.dto.MembershipCardVO;
import com.boxinggym.entity.MembershipCard;
import com.boxinggym.service.MembershipCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员卡定义Controller
 */
@Tag(name = "会员卡定义管理")
@RestController
@RequestMapping("/api/membership-card")
@RequiredArgsConstructor
public class MembershipCardController {

    private final MembershipCardService membershipCardService;

    @Operation(summary = "获取所有在售卡片")
    @GetMapping("/available")
    public Result<List<MembershipCardVO>> listAvailableCards() {
        return Result.success(membershipCardService.listAvailableCards());
    }

    @Operation(summary = "按分类获取卡片")
    @GetMapping("/category/{category}")
    public Result<List<MembershipCardVO>> listByCategory(
            @Parameter(description = "卡分类: 1-团课期限卡, 2-团课次卡, 3-私教次卡") @PathVariable Integer category) {
        return Result.success(membershipCardService.listByCategory(category));
    }

    @Operation(summary = "获取卡片详情")
    @GetMapping("/{id}")
    public Result<MembershipCardVO> getDetail(
            @Parameter(description = "卡片ID") @PathVariable Long id) {
        return Result.success(membershipCardService.getDetail(id));
    }

    @Operation(summary = "分页查询卡片")
    @GetMapping("/page")
    public Result<Page<MembershipCardVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "卡分类") @RequestParam(required = false) Integer cardCategory,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<MembershipCard> page = new Page<>(current, size);
        return Result.success(membershipCardService.page(page, cardCategory, status));
    }

    @Operation(summary = "创建卡片")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MembershipCardDTO dto) {
        return Result.success(membershipCardService.create(dto));
    }

    @Operation(summary = "更新卡片")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "卡片ID") @PathVariable Long id,
            @Valid @RequestBody MembershipCardDTO dto) {
        membershipCardService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "更新卡片状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "卡片ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        membershipCardService.updateStatus(id, status);
        return Result.success();
    }
}
