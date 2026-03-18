package com.boxinggym.service;

import com.boxinggym.dto.*;
import com.boxinggym.entity.MemberCard;

import java.util.List;

/**
 * 会员持卡服务接口
 */
public interface MemberCardService {

    /**
     * 购买会员卡
     *
     * @param dto 购卡请求
     * @return 会员卡ID
     */
    Long purchaseCard(PurchaseCardDTO dto);

    /**
     * 激活会员卡
     *
     * @param dto 激活请求
     */
    void activateCard(ActivateCardDTO dto);

    /**
     * 获取会员所有卡片
     *
     * @param memberId 会员ID
     * @return 卡片列表
     */
    List<MemberCardVO> getMemberCards(Long memberId);

    /**
     * 获取会员可用的团课卡（期限卡优先）
     *
     * @param memberId 会员ID
     * @return 可用卡片列表
     */
    List<MemberCardVO> getAvailableGroupCards(Long memberId);

    /**
     * 获取会员可用的私教卡
     *
     * @param memberId 会员ID
     * @return 可用卡片列表
     */
    List<MemberCardVO> getAvailablePrivateCards(Long memberId);

    /**
     * 签到扣次
     *
     * @param memberCardId 会员卡ID
     * @param scheduleId 排课ID
     * @param trainingRecordId 训练记录ID
     */
    void deductSession(Long memberCardId, Long scheduleId, Long trainingRecordId);

    /**
     * 作废卡片
     *
     * @param dto 作废请求
     */
    void voidCard(VoidCardDTO dto);

    /**
     * 获取卡片详情
     *
     * @param id 卡片ID
     * @return 卡片详情
     */
    MemberCardVO getDetail(Long id);

    /**
     * 检查并更新过期卡片状态（定时任务）
     */
    void checkAndUpdateExpiredCards();

    /**
     * 检查激活期限并作废超期未激活的卡（定时任务）
     */
    void checkActivationDeadline();

    /**
     * 根据ID获取卡片实体
     *
     * @param id 卡片ID
     * @return 卡片实体
     */
    MemberCard getEntityById(Long id);

    /**
     * 自动选择最佳团课卡进行签到
     *
     * @param memberId 会员ID
     * @return 最佳卡片ID，如果没有可用卡片返回null
     */
    Long selectBestGroupCard(Long memberId);

    /**
     * 验证卡片是否可用于签到
     *
     * @param memberCardId 会员卡ID
     * @param memberId 会员ID
     * @param isPrivateClass 是否为私教课
     * @return 是否可用
     */
    boolean validateCardForCheckin(Long memberCardId, Long memberId, boolean isPrivateClass);
}
