package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boxinggym.dto.MembershipCardDTO;
import com.boxinggym.dto.MembershipCardVO;
import com.boxinggym.entity.MembershipCard;

import java.util.List;

/**
 * 会员卡定义服务接口
 */
public interface MembershipCardService {

    /**
     * 获取所有在售卡片
     *
     * @return 在售卡片列表
     */
    List<MembershipCardVO> listAvailableCards();

    /**
     * 按分类获取卡片
     *
     * @param category 卡分类
     * @return 卡片列表
     */
    List<MembershipCardVO> listByCategory(Integer category);

    /**
     * 获取卡片详情
     *
     * @param id 卡片ID
     * @return 卡片详情
     */
    MembershipCardVO getDetail(Long id);

    /**
     * 分页查询卡片
     *
     * @param page 分页参数
     * @param cardCategory 卡分类（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<MembershipCardVO> page(Page<MembershipCard> page, Integer cardCategory, Integer status);

    /**
     * 创建卡片
     *
     * @param dto 卡片DTO
     * @return 卡片ID
     */
    Long create(MembershipCardDTO dto);

    /**
     * 更新卡片
     *
     * @param id 卡片ID
     * @param dto 卡片DTO
     */
    void update(Long id, MembershipCardDTO dto);

    /**
     * 更新卡片状态
     *
     * @param id 卡片ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据ID获取卡片实体
     *
     * @param id 卡片ID
     * @return 卡片实体
     */
    MembershipCard getById(Long id);

    /**
     * 根据卡编码获取卡片
     *
     * @param cardCode 卡编码
     * @return 卡片实体
     */
    MembershipCard getByCardCode(String cardCode);
}
