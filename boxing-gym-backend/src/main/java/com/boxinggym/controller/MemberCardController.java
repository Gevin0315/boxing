package com.boxinggym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.common.Result;
import com.boxinggym.dto.*;
import com.boxinggym.service.CardUsageRecordService;
import com.boxinggym.service.MemberCardService;
import com.boxinggym.vo.PaymentResultVO;
import com.boxinggym.vo.PendingOrderVO;
import com.boxinggym.vo.PurchaseCardResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员持卡管理Controller
 */
@Tag(name = "会员持卡管理")
@RestController
@RequestMapping("/api/member-card")
@RequiredArgsConstructor
public class MemberCardController {

    private final MemberCardService memberCardService;
    private final CardUsageRecordService cardUsageRecordService;

    @Operation(summary = "购买会员卡")
    @PostMapping("/purchase")
    public Result<PurchaseCardResultVO> purchaseCard(@Valid @RequestBody PurchaseCardDTO dto) {
        PurchaseCardResultVO result = memberCardService.purchaseCard(dto);
        return Result.success(result);
    }

    @Operation(summary = "确认支付")
    @PostMapping("/payment/confirm")
    public Result<PaymentResultVO> confirmPayment(@Valid @RequestBody ConfirmPaymentDTO dto) {
        PaymentResultVO result = memberCardService.confirmPayment(dto);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/payment/cancel")
    public Result<PaymentResultVO> cancelOrder(@Valid @RequestBody CancelOrderDTO dto) {
        PaymentResultVO result = memberCardService.cancelOrder(dto);
        return Result.success(result);
    }

    @Operation(summary = "获取会员待支付订单")
    @GetMapping("/payment/pending")
    public Result<List<PendingOrderVO>> getPendingOrders(@RequestParam Long memberId) {
        List<PendingOrderVO> orders = memberCardService.getPendingOrders(memberId);
        return Result.success(orders);
    }

    @Operation(summary = "激活会员卡")
    @PostMapping("/activate")
    public Result<Void> activateCard(@Valid @RequestBody ActivateCardDTO dto) {
        memberCardService.activateCard(dto);
        return Result.success();
    }

    @Operation(summary = "作废会员卡")
    @PostMapping("/void")
    public Result<Void> voidCard(@Valid @RequestBody VoidCardDTO dto) {
        memberCardService.voidCard(dto);
        return Result.success();
    }

    @Operation(summary = "获取会员所有卡片")
    @GetMapping("/member/{memberId}")
    public Result<List<MemberCardVO>> getMemberCards(
            @Parameter(description = "会员ID") @PathVariable Long memberId) {
        return Result.success(memberCardService.getMemberCards(memberId));
    }

    @Operation(summary = "获取会员可用的团课卡")
    @GetMapping("/member/{memberId}/available-group")
    public Result<List<MemberCardVO>> getAvailableGroupCards(
            @Parameter(description = "会员ID") @PathVariable Long memberId) {
        return Result.success(memberCardService.getAvailableGroupCards(memberId));
    }

    @Operation(summary = "获取会员可用的私教卡")
    @GetMapping("/member/{memberId}/available-private")
    public Result<List<MemberCardVO>> getAvailablePrivateCards(
            @Parameter(description = "会员ID") @PathVariable Long memberId) {
        return Result.success(memberCardService.getAvailablePrivateCards(memberId));
    }

    @Operation(summary = "获取卡片详情")
    @GetMapping("/{id}")
    public Result<MemberCardVO> getDetail(
            @Parameter(description = "卡片ID") @PathVariable Long id) {
        return Result.success(memberCardService.getDetail(id));
    }

    @Operation(summary = "获取卡片使用记录")
    @GetMapping("/{id}/usage-records")
    public Result<List<CardUsageRecordVO>> getCardUsageRecords(
            @Parameter(description = "会员卡ID") @PathVariable Long id) {
        return Result.success(cardUsageRecordService.getCardUsageRecords(id));
    }

    @Operation(summary = "分页获取卡片使用记录")
    @GetMapping("/usage-records/page")
    public Result<Page<CardUsageRecordVO>> pageUsageRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "会员卡ID") @RequestParam(required = false) Long memberCardId,
            @Parameter(description = "会员ID") @RequestParam(required = false) Long memberId) {
        Page<?> page = new Page<>(current, size);
        return Result.success(cardUsageRecordService.page(page, memberCardId, memberId));
    }

    @Operation(summary = "验证卡片是否可用于签到")
    @GetMapping("/{memberCardId}/validate-checkin")
    public Result<Boolean> validateCardForCheckin(
            @Parameter(description = "会员卡ID") @PathVariable Long memberCardId,
            @Parameter(description = "会员ID") @RequestParam Long memberId,
            @Parameter(description = "是否为私教课") @RequestParam Boolean isPrivateClass) {
        return Result.success(memberCardService.validateCardForCheckin(memberCardId, memberId, isPrivateClass));
    }
}
