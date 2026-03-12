package com.boxinggym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxinggym.common.Result;
import com.boxinggym.dto.FinanceOrderDTO;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.entity.Member;
import com.boxinggym.service.FinanceOrderService;
import com.boxinggym.service.MemberService;
import com.boxinggym.utils.ResponseAssembler;
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
import java.util.stream.Collectors;

import static com.boxinggym.common.Result.*;

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
    private final MemberService memberService;

    /**
     * 查询所有订单
     */
    @Operation(summary = "查询所有订单")
    @GetMapping("/list")
    public Result<List<FinanceOrderDTO>> list() {
        List<FinanceOrder> list = financeOrderService.lambdaQuery()
                .orderByDesc(FinanceOrder::getCreateTime)
                .list();

        // 收集所有关联ID
        List<Long> memberIds = list.stream()
                .map(FinanceOrder::getMemberId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询关联数据
        Map<Long, Member> memberMap = new HashMap<>();
        if (!memberIds.isEmpty()) {
            List<Member> members = memberService.listByIds(memberIds);
            Map<Long, Member> tempMemberMap = members.stream()
                    .collect(Collectors.toMap(Member::getId, m -> m));
            memberMap.putAll(tempMemberMap);
        }

        // 组装 DTO 列表
        List<FinanceOrderDTO> dtoList = list.stream().map(order -> ResponseAssembler.toFinanceOrderDTO(
                order,
                memberMap.get(order.getMemberId()),
                null,
                null, null, null
        )).collect(Collectors.toList());

        return Result.success(dtoList);
    }

    /**
     * 根据ID查询订单
     */
    @Operation(summary = "根据ID查询订单")
    @GetMapping("/{id}")
    public Result<FinanceOrderDTO> getById(@PathVariable Long id) {
        FinanceOrder order = financeOrderService.getById(id);
        if (order == null) {
            return notFound("订单不存在");
        }

        Member member = order.getMemberId() != null ? memberService.getById(order.getMemberId()) : null;

        FinanceOrderDTO dto = ResponseAssembler.toFinanceOrderDTO(
                order, member, null,
                null, null, null
        );

        return Result.success(dto);
    }

    /**
     * 根据会员ID查询订单
     */
    @Operation(summary = "根据会员ID查询订单")
    @GetMapping("/member/{memberId}")
    public Result<List<FinanceOrderDTO>> getByMemberId(@PathVariable Long memberId) {
        Member member = memberService.getById(memberId);
        if (member == null) {
            return notFound("会员不存在");
        }

        List<FinanceOrder> list = financeOrderService.lambdaQuery()
                .eq(FinanceOrder::getMemberId, memberId)
                .orderByDesc(FinanceOrder::getCreateTime)
                .list();

        // 组装 DTO 列表
        List<FinanceOrderDTO> dtoList = list.stream().map(order -> ResponseAssembler.toFinanceOrderDTO(
                order,
                member,
                null,
                null, null, null
        )).collect(Collectors.toList());

        return Result.success(dtoList);
    }

    /**
     * 新增订单
     */
    @Operation(summary = "新增订单")
    @PostMapping
    public Result<String> add(@RequestBody FinanceOrder order) {
        boolean success = financeOrderService.save(order);
        return success ? success("新增成功") : fail("新增失败");
    }

    /**
     * 删除订单
     */
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = financeOrderService.removeById(id);
        return success ? success("删除成功") : fail("删除失败");
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
            return notFound("订单不存在");
        }
        if (order.getType() != 1) {
            return fail("只有充值订单可以支付");
        }

        // 更新支付方式和状态
        boolean success = financeOrderService.lambdaUpdate()
                .eq(FinanceOrder::getId, id)
                .set(FinanceOrder::getPayMethod, payMethod)
                .set(FinanceOrder::getPaymentStatus, 1)
                .set(FinanceOrder::getPaidAmount, amount)
                .update();

        return success ? success("支付成功") : fail("支付失败");
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
            return notFound("订单不存在");
        }
        if (order.getType() != 1) {
            return fail("只有充值订单可以退款");
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
        return success ? success("退款成功") : fail("退款失败");
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

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecharge", totalRecharge);
        stats.put("totalRefund", totalRefund);
        stats.put("totalIncome", totalIncome);
        stats.put("orderCount", orders.size());
        return success(stats);
    }

    /**
     * 生成订单号
     */
    @Operation(summary = "生成订单号")
    @GetMapping("/generate-no")
    public Result<String> generateOrderNo() {
        return success("ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
    }
}
