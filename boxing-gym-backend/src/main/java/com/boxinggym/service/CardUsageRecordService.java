package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.CardUsageRecordVO;

import java.util.List;

/**
 * 会员卡使用记录服务接口
 */
public interface CardUsageRecordService {

    /**
     * 记录卡片使用
     *
     * @param memberCardId 会员卡ID
     * @param memberId 会员ID
     * @param usageType 使用类型
     * @param scheduleId 排课ID
     * @param trainingRecordId 训练记录ID
     * @param sessionsBefore 操作前次数
     * @param sessionsAfter 操作后次数
     * @param remark 备注
     */
    void recordUsage(Long memberCardId, Long memberId, Integer usageType,
                     Long scheduleId, Long trainingRecordId,
                     Integer sessionsBefore, Integer sessionsAfter, String remark);

    /**
     * 获取卡片使用记录
     *
     * @param memberCardId 会员卡ID
     * @return 使用记录列表
     */
    List<CardUsageRecordVO> getCardUsageRecords(Long memberCardId);

    /**
     * 分页获取卡片使用记录
     *
     * @param page 分页参数
     * @param memberCardId 会员卡ID
     * @param memberId 会员ID
     * @return 分页结果
     */
    Page<CardUsageRecordVO> page(Page<?> page, Long memberCardId, Long memberId);
}
