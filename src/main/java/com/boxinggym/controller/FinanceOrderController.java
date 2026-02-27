package com.boxinggym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxinggym.common.Result;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.service.FinanceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务订单 Controller
 */
@Tag(name = "财务订单管理", description = "财务订单相关接口")
@RestController
@RequestMapping("/api/finance-order")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
public class FinanceOrderController {

    private final FinanceOrderService financeOrderService;

    /**
     * 查询所有订单
     */
    @Operation(summary = "查询所有订单")
    @GetMapping("/list")
    public Result<List<FinanceOrder>> list() {
        List<FinanceOrder> list = financeOrderService.lambdaQuery()
                .orderByDesc(FinanceOrder::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 根据ID查询订单
     */
    @Operation(summary = "根据ID查询订单")
    @GetMapping("/{id}")
    public Result<FinanceOrder> getById(@PathVariable Long id) {
        FinanceOrder order = financeOrderService.getById(id);
        return Result.success(order);
    }

    /**
     * 根据会员ID查询订单
     */
    @Operation(summary = "根据会员ID查询订单")
    @GetMapping("/member/{memberId}")
    public Result<List<FinanceOrder>> getByMemberId(@PathVariable Long memberId) {
        List<FinanceOrder> list = financeOrderService.lambdaQuery()
                .eq(FinanceOrder::getMemberId, memberId)
                .orderByDesc(FinanceOrder::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 新增订单
     */
    @Operation(summary = "新增订单")
    @PostMapping
    public Result<String> add(@RequestBody FinanceOrder order) {
        boolean success = financeOrderService.save(order);
        return success ? Result.success("新增成功") : Result.fail("新增失败");
    }

    /**
     * 删除订单
     */
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = financeOrderService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 支付订单
     */
    @Operation(summary = "支付订单")
    @Transactional
    @PostMapping("/pay")
    public Result<String> payOrder(@RequestParam Long id, @RequestParam Integer payMethod, @RequestParam(required = false) BigDecimal amount) {
        FinanceOrder order = financeOrderService.getById(id);
        if (order == null) {
            return Result.notFound("订单不存在");
        }

        if (order.getType() != 1) {
            return Result.fail("只有充值订单可以支付");
        }

        // 更新支付方式
        boolean success = financeOrderService.lambdaUpdate()
                .eq(FinanceOrder::getId, id)
                .set(FinanceOrder::getPayMethod, payMethod)
                .update();

        return success ? Result.success("支付成功") : Result.fail("支付失败");
    }

    /**
     * 退款订单
     */
    @Operation(summary = "退款订单")
    @Transactional
    @PostMapping("/refund")
    public Result<String> refundOrder(@RequestParam Long id, @RequestParam(required = false) String reason) {
        FinanceOrder order = financeOrderService.getById(id);
        if (order == null) {
            return Result.notFound("订单不存在");
        }

        if (order.getType() != 1) {
            return Result.fail("只有充值订单可以退款");
        }

        // 创建退款订单
        FinanceOrder refundOrder = new FinanceOrder();
        refundOrder.setOrderNo("REF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        refundOrder.setMemberId(order.getMemberId());
        refundOrder.setAmount(order.getAmount().negate());
        refundOrder.setType(2); // 退款
        refundOrder.setPayMethod(5); // 系统扣减
        refundOrder.setRemark(reason != null ? reason : "退款");
        refundOrder.setCreateTime(LocalDateTime.now());
        boolean success = financeOrderService.save(refundOrder);

        return success ? Result.success("退款成功") : Result.fail("退款失败");
    }

    /**
     * 获取财务统计
     */
    @Operation(summary = "获取财务统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(FinanceOrder::getCreateTime, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(FinanceOrder::getCreateTime, endDate);
        }

        List<FinanceOrder> orders = financeOrderService.list(wrapper);

        Map<String, Object> stats = new HashMap<>();
        BigDecimal totalRecharge = BigDecimal.ZERO;
        BigDecimal totalRefund = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;

        for (FinanceOrder order : orders) {
            if (order.getType() == 1) {
                totalRecharge = totalRecharge.add(order.getAmount());
            } else if (order.getType() == 2) {
                totalRefund = totalRefund.add(order.getAmount().abs());
            }
        }
        totalIncome = totalRecharge.subtract(totalRefund);

        stats.put("totalRecharge", totalRecharge);
        stats.put("totalRefund", totalRefund);
        stats.put("totalIncome", totalIncome);
        stats.put("orderCount", orders.size());

        return Result.success(stats);
    }

    /**
     * 生成订单号
     */
    @Operation(summary = "生成订单号")
    @GetMapping("/generate-no")
    public Result<String> generateOrderNo() {
        return Result.success("ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
    }
}
