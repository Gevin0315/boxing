package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.common.BusinessException;
import com.boxinggym.dto.MembershipCardDTO;
import com.boxinggym.dto.MembershipCardVO;
import com.boxinggym.entity.MembershipCard;
import com.boxinggym.enums.CardCategoryEnum;
import com.boxinggym.enums.CardTypeEnum;
import com.boxinggym.mapper.MembershipCardMapper;
import com.boxinggym.service.MembershipCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员卡定义服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipCardServiceImpl extends ServiceImpl<MembershipCardMapper, MembershipCard> implements MembershipCardService {

    @Override
    public List<MembershipCardVO> listAvailableCards() {
        LambdaQueryWrapper<MembershipCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipCard::getStatus, 1)
                .orderByAsc(MembershipCard::getSortOrder)
                .orderByAsc(MembershipCard::getPrice);
        List<MembershipCard> cards = list(wrapper);
        return cards.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<MembershipCardVO> listByCategory(Integer category) {
        LambdaQueryWrapper<MembershipCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipCard::getCardCategory, category)
                .eq(MembershipCard::getStatus, 1)
                .orderByAsc(MembershipCard::getSortOrder)
                .orderByAsc(MembershipCard::getPrice);
        List<MembershipCard> cards = list(wrapper);
        return cards.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public MembershipCardVO getDetail(Long id) {
        MembershipCard card = getById(id);
        if (card == null) {
            throw new BusinessException("卡片不存在");
        }
        return convertToVO(card);
    }

    @Override
    public Page<MembershipCardVO> page(Page<MembershipCard> page, Integer cardCategory, Integer status) {
        LambdaQueryWrapper<MembershipCard> wrapper = new LambdaQueryWrapper<>();
        if (cardCategory != null) {
            wrapper.eq(MembershipCard::getCardCategory, cardCategory);
        }
        if (status != null) {
            wrapper.eq(MembershipCard::getStatus, status);
        }
        wrapper.orderByAsc(MembershipCard::getSortOrder).orderByDesc(MembershipCard::getCreateTime);
        Page<MembershipCard> result = page(page, wrapper);
        Page<MembershipCardVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MembershipCardDTO dto) {
        // 检查卡编码是否已存在
        if (getByCardCode(dto.getCardCode()) != null) {
            throw new BusinessException("卡编码已存在: " + dto.getCardCode());
        }
        MembershipCard card = new MembershipCard();
        BeanUtils.copyProperties(dto, card);
        card.setCreateTime(LocalDateTime.now());
        card.setUpdateTime(LocalDateTime.now());
        if (card.getStatus() == null) {
            card.setStatus(1);
        }
        if (card.getActivationDeadlineDays() == null) {
            card.setActivationDeadlineDays(30);
        }
        save(card);
        return card.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MembershipCardDTO dto) {
        MembershipCard card = getById(id);
        if (card == null) {
            throw new BusinessException("卡片不存在");
        }
        // 如果修改了卡编码，检查是否已存在
        if (!card.getCardCode().equals(dto.getCardCode())) {
            if (getByCardCode(dto.getCardCode()) != null) {
                throw new BusinessException("卡编码已存在: " + dto.getCardCode());
            }
        }
        BeanUtils.copyProperties(dto, card);
        card.setId(id);
        card.setUpdateTime(LocalDateTime.now());
        updateById(card);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        MembershipCard card = getById(id);
        if (card == null) {
            throw new BusinessException("卡片不存在");
        }
        card.setStatus(status);
        card.setUpdateTime(LocalDateTime.now());
        updateById(card);
    }

    @Override
    public MembershipCard getById(Long id) {
        return super.getById(id);
    }

    @Override
    public MembershipCard getByCardCode(String cardCode) {
        LambdaQueryWrapper<MembershipCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipCard::getCardCode, cardCode);
        return getOne(wrapper);
    }

    /**
     * 转换为VO
     */
    private MembershipCardVO convertToVO(MembershipCard card) {
        MembershipCardVO vo = new MembershipCardVO();
        BeanUtils.copyProperties(card, vo);
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
        vo.setStatusDesc(card.getStatus() == 1 ? "在售" : "停售");
        return vo;
    }
}
