package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.common.BusinessException;
import com.boxinggym.dto.*;
import com.boxinggym.entity.*;
import com.boxinggym.enums.*;
import com.boxinggym.mapper.*;
import com.boxinggym.service.*;
import com.boxinggym.utils.SecurityUtil;
import com.boxinggym.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员持卡服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements MemberCardService {

    private final MembershipCardService membershipCardService;
    private final CardUsageRecordService cardUsageRecordService;
    private final FinanceOrderMapper financeOrderMapper;
    private final MemberMapper memberMapper;

    private static final DateTimeFormatter CARD_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseCardResultVO purchaseCard(PurchaseCardDTO dto) {
        // 获取会员
        Member member = memberMapper.selectById(dto.getMemberId());
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        // 获取卡片定义
        MembershipCard cardDef = membershipCardService.getById(dto.getCardId());
        if (cardDef == null) {
            throw new BusinessException("卡片定义不存在");
        }
        if (cardDef.getStatus() != 1) {
            throw new BusinessException("该卡片已停售");
        }

        BigDecimal paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : cardDef.getPrice();

        PurchaseCardResultVO result = new PurchaseCardResultVO();

        // 判断支付方式：现金(3)/刷卡(4)即时完成，微信(1)/支付宝(2)待支付
        boolean immediatePayment = dto.getPayMethod() == 3 || dto.getPayMethod() == 4;

        if (immediatePayment) {
            // 即时完成：创建卡片和订单
            MemberCard memberCard = createMemberCard(dto.getMemberId(), cardDef, dto.getRemark());
            FinanceOrder order = createFinanceOrder(dto.getMemberId(), paidAmount, dto.getPayMethod(),
                    cardDef.getCardName(), memberCard.getId(), dto.getCardId());
            memberCard.setOrderId(order.getId());
            updateById(memberCard);

            result.setCardId(memberCard.getId());
            result.setOrderId(order.getId());
            result.setStatus("completed");
        } else {
            // 待支付：只创建订单
            FinanceOrder order = createPendingOrder(dto.getMemberId(), paidAmount, dto.getPayMethod(),
                    cardDef.getCardName(), dto.getCardId());

            result.setCardId(null);
            result.setOrderId(order.getId());
            result.setStatus("pending");
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateCard(ActivateCardDTO dto) {
        MemberCard memberCard = getEntityById(dto.getMemberCardId());
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (!CardStatusEnum.INACTIVE.getCode().equals(memberCard.getStatus())) {
            throw new BusinessException("只有未激活的卡片才能激活");
        }
        // 检查是否超过激活期限
        if (memberCard.getActivationDeadline() != null && LocalDate.now().isAfter(memberCard.getActivationDeadline())) {
            throw new BusinessException("已超过激活期限，无法激活");
        }
        // 获取卡片定义
        MembershipCard cardDef = membershipCardService.getById(memberCard.getCardId());
        if (cardDef == null) {
            throw new BusinessException("卡片定义不存在");
        }
        // 激活卡片
        memberCard.setStatus(CardStatusEnum.ACTIVE.getCode());
        memberCard.setActivationTime(LocalDateTime.now());
        memberCard.setStartDate(LocalDate.now());
        // 设置失效日期
        CardTypeEnum cardType = CardTypeEnum.fromCode(memberCard.getCardType());
        if (cardType.isTimeCard()) {
            // 期限卡：根据durationDays设置
            int durationDays = cardDef.getDurationDays() != null ? cardDef.getDurationDays() : 30;
            memberCard.setExpireDate(LocalDate.now().plusDays(durationDays));
        } else {
            // 次卡：根据validityDaysAfterActivation设置
            int validDays = cardDef.getValidityDaysAfterActivation() != null ? cardDef.getValidityDaysAfterActivation() : 30;
            memberCard.setExpireDate(LocalDate.now().plusDays(validDays));
        }
        memberCard.setUpdateTime(LocalDateTime.now());
        updateById(memberCard);
        // 记录使用
        cardUsageRecordService.recordUsage(
                memberCard.getId(),
                memberCard.getMemberId(),
                CardUsageTypeEnum.ACTIVATE.getCode(),
                null, null,
                memberCard.getRemainingSessions(),
                memberCard.getRemainingSessions(),
                dto.getRemark()
        );
    }

    @Override
    public List<MemberCardVO> getMemberCards(Long memberId) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getMemberId, memberId)
                .orderByDesc(MemberCard::getCreateTime);
        List<MemberCard> cards = list(wrapper);
        return convertToVOList(cards);
    }

    @Override
    public List<MemberCardVO> getAvailableGroupCards(Long memberId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getMemberId, memberId)
                .eq(MemberCard::getStatus, CardStatusEnum.ACTIVE.getCode())
                .in(MemberCard::getCardCategory, CardCategoryEnum.GROUP_TIME.getCode(), CardCategoryEnum.GROUP_SESSION.getCode())
                .ge(MemberCard::getExpireDate, today)
                .orderByAsc(MemberCard::getCardCategory) // 期限卡优先
                .orderByAsc(MemberCard::getExpireDate);
        // 次卡需要检查剩余次数
        List<MemberCard> cards = list(wrapper);
        // 过滤次卡：剩余次数大于0
        return cards.stream()
                .filter(card -> {
                    if (CardCategoryEnum.GROUP_SESSION.getCode().equals(card.getCardCategory())) {
                        return card.getRemainingSessions() != null && card.getRemainingSessions() > 0;
                    }
                    return true;
                })
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberCardVO> getAvailablePrivateCards(Long memberId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getMemberId, memberId)
                .eq(MemberCard::getStatus, CardStatusEnum.ACTIVE.getCode())
                .eq(MemberCard::getCardCategory, CardCategoryEnum.PRIVATE_SESSION.getCode())
                .ge(MemberCard::getExpireDate, today)
                .orderByAsc(MemberCard::getExpireDate);
        List<MemberCard> cards = list(wrapper);
        return cards.stream()
                .filter(card -> card.getRemainingSessions() != null && card.getRemainingSessions() > 0)
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductSession(Long memberCardId, Long scheduleId, Long trainingRecordId) {
        MemberCard memberCard = getEntityById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (!CardStatusEnum.ACTIVE.getCode().equals(memberCard.getStatus())) {
            throw new BusinessException("会员卡未激活或已过期");
        }
        Integer sessionsBefore = memberCard.getRemainingSessions();
        // 期限卡不扣次数
        if (!CardCategoryEnum.GROUP_TIME.getCode().equals(memberCard.getCardCategory())) {
            if (sessionsBefore == null || sessionsBefore <= 0) {
                throw new BusinessException("会员卡次数不足");
            }
            memberCard.setRemainingSessions(sessionsBefore - 1);
        }
        memberCard.setLastCheckinTime(LocalDateTime.now());
        memberCard.setUpdateTime(LocalDateTime.now());
        updateById(memberCard);
        // 记录使用
        cardUsageRecordService.recordUsage(
                memberCardId,
                memberCard.getMemberId(),
                CardUsageTypeEnum.CHECKIN.getCode(),
                scheduleId,
                trainingRecordId,
                sessionsBefore,
                memberCard.getRemainingSessions(),
                "签到扣次"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidCard(VoidCardDTO dto) {
        MemberCard memberCard = getEntityById(dto.getMemberCardId());
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (CardStatusEnum.VOIDED.getCode().equals(memberCard.getStatus())) {
            throw new BusinessException("卡片已作废");
        }
        if (CardStatusEnum.EXPIRED.getCode().equals(memberCard.getStatus())) {
            throw new BusinessException("已过期的卡片无需作废");
        }
        Integer sessionsBefore = memberCard.getRemainingSessions();
        memberCard.setStatus(CardStatusEnum.VOIDED.getCode());
        memberCard.setRemark(dto.getReason());
        memberCard.setUpdateTime(LocalDateTime.now());
        updateById(memberCard);
        // 记录使用
        cardUsageRecordService.recordUsage(
                memberCard.getId(),
                memberCard.getMemberId(),
                CardUsageTypeEnum.VOID.getCode(),
                null, null,
                sessionsBefore,
                0,
                dto.getReason()
        );
    }

    @Override
    public MemberCardVO getDetail(Long id) {
        MemberCard card = getEntityById(id);
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        return convertToVO(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateExpiredCards() {
        LocalDate today = LocalDate.now();
        // 查询生效中但已过期的卡片
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getStatus, CardStatusEnum.ACTIVE.getCode())
                .lt(MemberCard::getExpireDate, today);
        List<MemberCard> expiredCards = list(wrapper);
        for (MemberCard card : expiredCards) {
            Integer sessionsBefore = card.getRemainingSessions();
            card.setStatus(CardStatusEnum.EXPIRED.getCode());
            card.setUpdateTime(LocalDateTime.now());
            updateById(card);
            // 记录使用
            cardUsageRecordService.recordUsage(
                    card.getId(),
                    card.getMemberId(),
                    CardUsageTypeEnum.EXPIRE.getCode(),
                    null, null,
                    sessionsBefore,
                    0,
                    "系统自动过期"
            );
            log.info("卡片过期: cardId={}, memberId={}", card.getId(), card.getMemberId());
        }
        log.info("过期卡片检查完成，共{}张", expiredCards.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkActivationDeadline() {
        LocalDate today = LocalDate.now();
        // 查询未激活且超过激活期限的卡片
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getStatus, CardStatusEnum.INACTIVE.getCode())
                .lt(MemberCard::getActivationDeadline, today);
        List<MemberCard> voidedCards = list(wrapper);
        for (MemberCard card : voidedCards) {
            Integer sessionsBefore = card.getRemainingSessions();
            card.setStatus(CardStatusEnum.VOIDED.getCode());
            card.setRemark("超过激活期限，系统自动作废");
            card.setUpdateTime(LocalDateTime.now());
            updateById(card);
            // 记录使用
            cardUsageRecordService.recordUsage(
                    card.getId(),
                    card.getMemberId(),
                    CardUsageTypeEnum.VOID.getCode(),
                    null, null,
                    sessionsBefore,
                    0,
                    "超过激活期限，系统自动作废"
            );
            log.info("卡片超期未激活作废: cardId={}, memberId={}", card.getId(), card.getMemberId());
        }
        log.info("激活期限检查完成，共作废{}张", voidedCards.size());
    }

    @Override
    public MemberCard getEntityById(Long id) {
        return super.getById(id);
    }

    @Override
    public Long selectBestGroupCard(Long memberId) {
        List<MemberCardVO> availableCards = getAvailableGroupCards(memberId);
        if (availableCards.isEmpty()) {
            return null;
        }
        // 优先返回期限卡（卡分类为1）
        return availableCards.get(0).getId();
    }

    @Override
    public boolean validateCardForCheckin(Long memberCardId, Long memberId, boolean isPrivateClass) {
        MemberCard card = getEntityById(memberCardId);
        if (card == null) {
            return false;
        }
        if (!card.getMemberId().equals(memberId)) {
            return false;
        }
        if (!CardStatusEnum.ACTIVE.getCode().equals(card.getStatus())) {
            return false;
        }
        if (card.getExpireDate() != null && LocalDate.now().isAfter(card.getExpireDate())) {
            return false;
        }
        // 私教课检查
        if (isPrivateClass) {
            if (!CardCategoryEnum.PRIVATE_SESSION.getCode().equals(card.getCardCategory())) {
                return false;
            }
            if (card.getRemainingSessions() == null || card.getRemainingSessions() <= 0) {
                return false;
            }
        } else {
            // 团课检查
            if (!CardCategoryEnum.GROUP_TIME.getCode().equals(card.getCardCategory()) &&
                !CardCategoryEnum.GROUP_SESSION.getCode().equals(card.getCardCategory())) {
                return false;
            }
            // 次卡检查次数
            if (CardCategoryEnum.GROUP_SESSION.getCode().equals(card.getCardCategory())) {
                if (card.getRemainingSessions() == null || card.getRemainingSessions() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO confirmPayment(ConfirmPaymentDTO dto) {
        FinanceOrder order = financeOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单类型
        if (!OrderCategoryEnum.PURCHASE_CARD.getCode().equals(order.getOrderCategory())) {
            throw new BusinessException("非购卡订单");
        }

        // 验证订单状态（幂等性处理）
        if (PaymentStatusEnum.PAID.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单已支付，请勿重复确认");
        }
        if (PaymentStatusEnum.CANCELLED.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单已取消");
        }
        if (!PaymentStatusEnum.PENDING.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 验证卡片定义
        MembershipCard cardDef = membershipCardService.getById(order.getCardId());
        if (cardDef == null) {
            throw new BusinessException("卡片定义不存在");
        }
        if (cardDef.getStatus() != 1) {
            throw new BusinessException("该卡片已停售");
        }

        // 创建会员卡
        MemberCard memberCard = createMemberCard(order.getMemberId(), cardDef, "支付确认后创建");

        // 更新订单
        order.setMemberCardId(memberCard.getId());
        order.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
        order.setPaidAmount(order.getAmount());
        order.setUpdateTime(LocalDateTime.now());
        financeOrderMapper.updateById(order);

        // 关联双向ID
        memberCard.setOrderId(order.getId());
        updateById(memberCard);

        PaymentResultVO result = new PaymentResultVO();
        result.setCardId(memberCard.getId());
        result.setStatus("completed");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO cancelOrder(CancelOrderDTO dto) {
        FinanceOrder order = financeOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单类型
        if (!OrderCategoryEnum.PURCHASE_CARD.getCode().equals(order.getOrderCategory())) {
            throw new BusinessException("非购卡订单");
        }

        // 验证订单状态
        if (PaymentStatusEnum.PAID.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("已支付订单无法取消");
        }
        if (PaymentStatusEnum.CANCELLED.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单已取消");
        }
        if (!PaymentStatusEnum.PENDING.getCode().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 更新订单状态
        order.setPaymentStatus(PaymentStatusEnum.CANCELLED.getCode());
        if (dto.getReason() != null) {
            order.setRemark(order.getRemark() + " | 取消原因: " + dto.getReason());
        }
        order.setUpdateTime(LocalDateTime.now());
        financeOrderMapper.updateById(order);

        PaymentResultVO result = new PaymentResultVO();
        result.setStatus("cancelled");
        return result;
    }

    @Override
    public List<PendingOrderVO> getPendingOrders(Long memberId) {
        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceOrder::getMemberId, memberId)
                .eq(FinanceOrder::getPaymentStatus, PaymentStatusEnum.PENDING.getCode())
                .eq(FinanceOrder::getOrderCategory, OrderCategoryEnum.PURCHASE_CARD.getCode())
                .orderByDesc(FinanceOrder::getCreateTime);
        List<FinanceOrder> orders = financeOrderMapper.selectList(wrapper);

        return orders.stream().map(order -> {
            PendingOrderVO vo = new PendingOrderVO();
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setCardId(order.getCardId());
            vo.setAmount(order.getAmount());
            vo.setPayMethod(order.getPayMethod());
            vo.setCreateTime(order.getCreateTime());

            // 获取卡片名称
            MembershipCard cardDef = membershipCardService.getById(order.getCardId());
            if (cardDef != null) {
                vo.setCardName(cardDef.getCardName());
            }

            // 支付方式描述
            try {
                PayMethodEnum payMethod = PayMethodEnum.fromCode(order.getPayMethod());
                vo.setPayMethodDesc(payMethod.getDescription());
            } catch (IllegalArgumentException e) {
                vo.setPayMethodDesc("未知");
            }

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 创建会员卡
     *
     * @param memberId 会员ID
     * @param cardDef  卡片定义
     * @param remark   备注
     * @return 创建的会员卡
     */
    private MemberCard createMemberCard(Long memberId, MembershipCard cardDef, String remark) {
        MemberCard memberCard = new MemberCard();
        memberCard.setMemberId(memberId);
        memberCard.setCardId(cardDef.getId());
        memberCard.setCardCategory(cardDef.getCardCategory());
        memberCard.setCardType(cardDef.getCardType());
        memberCard.setStatus(CardStatusEnum.INACTIVE.getCode());
        if (cardDef.getSessionCount() != null) {
            memberCard.setTotalSessions(cardDef.getSessionCount());
            memberCard.setRemainingSessions(cardDef.getSessionCount());
        }
        memberCard.setPurchaseTime(LocalDateTime.now());
        int activationDays = cardDef.getActivationDeadlineDays() != null ? cardDef.getActivationDeadlineDays() : 30;
        memberCard.setActivationDeadline(LocalDate.now().plusDays(activationDays));
        memberCard.setRemark(remark);
        memberCard.setCreateTime(LocalDateTime.now());
        memberCard.setUpdateTime(LocalDateTime.now());
        save(memberCard);
        return memberCard;
    }

    /**
     * 创建已支付订单（即时完成）
     *
     * @param memberId    会员ID
     * @param amount      金额
     * @param payMethod   支付方式
     * @param cardName    卡片名称
     * @param memberCardId 会员卡ID
     * @param cardDefId   卡片定义ID
     * @return 创建的订单
     */
    private FinanceOrder createFinanceOrder(Long memberId, BigDecimal amount, Integer payMethod,
            String cardName, Long memberCardId, Long cardDefId) {
        FinanceOrder order = new FinanceOrder();
        order.setOrderNo("FO" + LocalDateTime.now().format(CARD_NO_FORMATTER));
        order.setMemberId(memberId);
        order.setAmount(amount);
        order.setType(OrderTypeEnum.RECHARGE.getCode());
        order.setPayMethod(payMethod);
        order.setRemark("购买" + cardName);
        order.setPaidAmount(amount);
        order.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
        order.setMemberCardId(memberCardId);
        order.setCardId(cardDefId);
        order.setOrderCategory(OrderCategoryEnum.PURCHASE_CARD.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        try {
            order.setCreateBy(SecurityUtil.getCurrentUserId());
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
        }
        financeOrderMapper.insert(order);
        return order;
    }

    /**
     * 创建待支付订单
     *
     * @param memberId  会员ID
     * @param amount    金额
     * @param payMethod 支付方式
     * @param cardName  卡片名称
     * @param cardDefId 卡片定义ID
     * @return 创建的订单
     */
    private FinanceOrder createPendingOrder(Long memberId, BigDecimal amount, Integer payMethod,
            String cardName, Long cardDefId) {
        FinanceOrder order = new FinanceOrder();
        order.setOrderNo("FO" + LocalDateTime.now().format(CARD_NO_FORMATTER));
        order.setMemberId(memberId);
        order.setAmount(amount);
        order.setType(OrderTypeEnum.RECHARGE.getCode());
        order.setPayMethod(payMethod);
        order.setRemark("购买" + cardName);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setPaymentStatus(PaymentStatusEnum.PENDING.getCode());
        order.setMemberCardId(null);
        order.setCardId(cardDefId);
        order.setOrderCategory(OrderCategoryEnum.PURCHASE_CARD.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        try {
            order.setCreateBy(SecurityUtil.getCurrentUserId());
        } catch (Exception e) {
            log.warn("获取当前用户ID失败", e);
        }
        financeOrderMapper.insert(order);
        return order;
    }

    /**
     * 批量转换为VO
     */
    private List<MemberCardVO> convertToVOList(List<MemberCard> cards) {
        if (cards.isEmpty()) {
            return List.of();
        }
        return cards.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private MemberCardVO convertToVO(MemberCard card) {
        MemberCardVO vo = new MemberCardVO();
        BeanUtils.copyProperties(card, vo);
        // 获取会员名称
        Member member = memberMapper.selectById(card.getMemberId());
        if (member != null) {
            vo.setMemberName(member.getName());
        }
        // 获取卡片定义信息
        MembershipCard cardDef = membershipCardService.getById(card.getCardId());
        if (cardDef != null) {
            vo.setCardName(cardDef.getCardName());
        }
        // 设置卡分类描述
        try {
            CardCategoryEnum category = CardCategoryEnum.fromCode(card.getCardCategory());
            vo.setCardCategoryDesc(category.getDescription());
        } catch (IllegalArgumentException e) {
            vo.setCardCategoryDesc("未知");
        }
        // 设置卡类型描述
        try {
            CardTypeEnum type = CardTypeEnum.fromCode(card.getCardType());
            vo.setCardTypeDesc(type.getDescription());
        } catch (IllegalArgumentException e) {
            vo.setCardTypeDesc("未知");
        }
        // 设置状态描述
        try {
            CardStatusEnum status = CardStatusEnum.fromCode(card.getStatus());
            vo.setStatusDesc(status.getDescription());
        } catch (IllegalArgumentException e) {
            vo.setStatusDesc("未知");
        }
        // 设置是否可以激活
        vo.setCanBeActivated(CardStatusEnum.INACTIVE.getCode().equals(card.getStatus()) &&
                (card.getActivationDeadline() == null || !LocalDate.now().isAfter(card.getActivationDeadline())));
        // 设置是否可以签到
        boolean canCheckin = CardStatusEnum.ACTIVE.getCode().equals(card.getStatus()) &&
                (card.getExpireDate() == null || !LocalDate.now().isAfter(card.getExpireDate()));
        if (canCheckin && !CardCategoryEnum.GROUP_TIME.getCode().equals(card.getCardCategory())) {
            canCheckin = card.getRemainingSessions() != null && card.getRemainingSessions() > 0;
        }
        vo.setCanBeUsedForCheckin(canCheckin);
        // 计算剩余天数
        if (card.getExpireDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), card.getExpireDate());
            vo.setRemainingDays((int) Math.max(0, days));
        }
        return vo;
    }
}
