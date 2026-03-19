package com.boxinggym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.common.Result;
import com.boxinggym.dto.BatchDeleteDTO;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.entity.Member;
import com.boxinggym.service.FinanceOrderService;
import com.boxinggym.service.MemberService;
import com.boxinggym.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 会员 Controller
 */
@Tag(name = "会员管理", description = "会员相关接口")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTION')")
public class MemberController {

    private final MemberService memberService;
    private final FinanceOrderService financeOrderService;

    /**
     * 分页查询会员
     */
    @Operation(summary = "分页查询会员")
    @GetMapping("/page")
    public Result<Page<Member>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        Page<Member> page = new Page<>(current, size);
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Member::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(Member::getPhone, phone);
        }
        memberService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 查询所有会员
     */
    @Operation(summary = "查询所有会员")
    @GetMapping("/list")
    public Result<java.util.List<Member>> list() {
        java.util.List<Member> list = memberService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询会员
     */
    @Operation(summary = "根据ID查询会员")
    @GetMapping("/{id}")
    public Result<Member> getById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return Result.success(member);
    }

    /**
     * 根据手机号查询会员
     */
    @Operation(summary = "根据手机号查询会员")
    @GetMapping("/phone/{phone}")
    public Result<Member> getByPhone(@PathVariable String phone) {
        return Result.success(memberService.lambdaQuery()
                .eq(Member::getPhone, phone)
                .one());
    }

    /**
     * 新增会员（自动生成会员号）
     */
    @Operation(summary = "新增会员")
    @PostMapping
    public Result<Member> add(@RequestBody Member member) {
        // 自动生成会员号
        member.setMemberNo(generateMemberNo());
        boolean success = memberService.save(member);
        return success ? Result.success(member) : Result.fail("新增失败");
    }

    /**
     * 修改会员
     */
    @Operation(summary = "修改会员")
    @PutMapping
    public Result<String> update(@RequestBody Member member) {
        boolean success = memberService.updateById(member);
        return success ? Result.success("修改成功") : Result.fail("修改失败");
    }

    /**
     * 删除会员
     */
    @Operation(summary = "删除会员")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Member member = memberService.getById(id);
        if (member == null) {
            return Result.notFound("会员不存在");
        }
        boolean success = memberService.removeById(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 批量删除会员
     */
    @Operation(summary = "批量删除会员")
    @DeleteMapping
    public Result<String> batchDelete(@Valid @RequestBody BatchDeleteDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Result.fail("ID列表不能为空");
        }
        boolean success = memberService.removeByIds(dto.getIds());
        return success ? Result.success("批量删除成功") : Result.fail("批量删除失败");
    }

    /**
     * 会员充值
     */
    @Operation(summary = "会员充值")
    @Transactional
    @PostMapping("/recharge")
    public Result<String> recharge(@RequestParam Long id, @RequestParam BigDecimal amount, @RequestParam Integer payMethod) {
        Member member = memberService.getById(id);
        if (member == null) {
            return Result.notFound("会员不存在");
        }

        // 更新会员余额
        member.setBalance(member.getBalance().add(amount));
        boolean success = memberService.updateById(member);

        // 创建财务订单
        if (success) {
            FinanceOrder order = new FinanceOrder();
            order.setOrderNo(generateOrderNo());
            order.setMemberId(id);
            order.setAmount(amount);
            order.setType(1); // 充值
            order.setPayMethod(payMethod);
            order.setRemark("会员充值");
            order.setCreateTime(LocalDateTime.now());
            order.setCreateBy(SecurityUtil.getCurrentUserId());
            financeOrderService.save(order);
        }

        return success ? Result.success("充值成功") : Result.fail("充值失败");
    }

    /**
     * 会员扣费
     */
    @Operation(summary = "会员扣费")
    @Transactional
    @PostMapping("/deduct")
    public Result<String> deduct(@RequestParam Long id, @RequestParam BigDecimal amount, @RequestParam(required = false) String remark) {
        Member member = memberService.getById(id);
        if (member == null) {
            return Result.notFound("会员不存在");
        }

        if (member.getBalance().compareTo(amount) < 0) {
            return Result.fail("余额不足");
        }

        // 更新会员余额
        member.setBalance(member.getBalance().subtract(amount));
        boolean success = memberService.updateById(member);

        // 创建财务订单
        if (success) {
            FinanceOrder order = new FinanceOrder();
            order.setOrderNo(generateOrderNo());
            order.setMemberId(id);
            order.setAmount(amount.negate());
            order.setType(3); // 课程消费
            order.setPayMethod(5); // 系统扣减
            order.setRemark(remark != null ? remark : "课程扣费");
            order.setCreateTime(LocalDateTime.now());
            order.setCreateBy(SecurityUtil.getCurrentUserId());
            financeOrderService.save(order);
        }

        return success ? Result.success("扣费成功") : Result.fail("扣费失败");
    }

    /**
     * 修改会员状态
     */
    @Operation(summary = "修改会员状态")
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean success = memberService.lambdaUpdate()
                .eq(Member::getId, id)
                .set(Member::getStatus, status)
                .update();
        return success ? Result.success("状态修改成功") : Result.fail("状态修改失败");
    }

    /**
     * 生成会员号（基于当前最大会员号+1）
     */
    private String generateMemberNo() {
        Member maxMember = memberService.lambdaQuery()
                .select(Member::getMemberNo)
                .orderByDesc(Member::getMemberNo)
                .last("LIMIT 1")
                .one();

        long nextNo = 1;
        if (maxMember != null && maxMember.getMemberNo() != null) {
            String maxNo = maxMember.getMemberNo();
            if (maxNo.startsWith("M")) {
                nextNo = Long.parseLong(maxNo.substring(1)) + 1;
            }
        }
        return "M" + String.format("%06d", nextNo);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
