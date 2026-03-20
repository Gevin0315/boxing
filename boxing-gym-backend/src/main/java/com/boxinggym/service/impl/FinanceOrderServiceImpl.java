package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.dto.FinanceOrderQueryDTO;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.entity.Member;
import com.boxinggym.mapper.FinanceOrderMapper;
import com.boxinggym.service.FinanceOrderService;
import com.boxinggym.service.MemberService;
import com.boxinggym.vo.FinanceOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务订单 Service 实现
 */
@Service
@RequiredArgsConstructor
public class FinanceOrderServiceImpl extends ServiceImpl<FinanceOrderMapper, FinanceOrder> implements FinanceOrderService {

    private final MemberService memberService;

    @Override
    public Page<FinanceOrderVO> page(FinanceOrderQueryDTO query) {
        Page<FinanceOrder> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();

        // 根据会员姓名查询符合条件的会员ID列表
        List<Long> memberIds = null;
        if (query.getMemberName() != null && !query.getMemberName().isEmpty()) {
            LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.like(Member::getName, query.getMemberName());
            List<Member> members = memberService.list(memberWrapper);
            memberIds = members.stream()
                    .map(Member::getId)
                    .collect(Collectors.toList());

            // 如果没有匹配的会员，返回空结果
            if (memberIds.isEmpty()) {
                Page<FinanceOrderVO> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
        }

        // 订单号模糊查询
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            wrapper.like(FinanceOrder::getOrderNo, query.getOrderNo());
        }
        // 订单类型精确查询
        if (query.getType() != null) {
            wrapper.eq(FinanceOrder::getType, query.getType());
        }
        // 会员ID条件
        if (memberIds != null) {
            wrapper.in(FinanceOrder::getMemberId, memberIds);
        }
        // 支付状态精确查询
        if (query.getPaymentStatus() != null) {
            wrapper.eq(FinanceOrder::getPaymentStatus, query.getPaymentStatus());
        }

        wrapper.orderByDesc(FinanceOrder::getCreateTime);
        Page<FinanceOrder> result = page(page, wrapper);

        // 批量查询关联的会员信息
        List<Long> resultMemberIds = result.getRecords().stream()
                .map(FinanceOrder::getMemberId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Member> memberMap = Collections.emptyMap();
        if (!resultMemberIds.isEmpty()) {
            List<Member> members = memberService.listByIds(resultMemberIds);
            memberMap = members.stream()
                    .collect(Collectors.toMap(Member::getId, m -> m));
        }

        // 转换为 VO
        Page<FinanceOrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        final Map<Long, Member> finalMemberMap = memberMap;
        List<FinanceOrderVO> voList = result.getRecords().stream()
                .map(order -> convertToVO(order, finalMemberMap.get(order.getMemberId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将实体转换为 VO
     *
     * @param order  订单实体
     * @param member 会员实体
     * @return VO
     */
    private FinanceOrderVO convertToVO(FinanceOrder order, Member member) {
        FinanceOrderVO vo = new FinanceOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setMemberId(order.getMemberId());
        vo.setType(order.getType());
        vo.setAmount(order.getAmount());
        vo.setPaidAmount(order.getPaidAmount());
        vo.setPayMethod(order.getPayMethod());
        vo.setPaymentStatus(order.getPaymentStatus());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());

        if (member != null) {
            vo.setMemberName(member.getName());
        }
        return vo;
    }
}
