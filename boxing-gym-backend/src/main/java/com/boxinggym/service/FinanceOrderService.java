package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.FinanceOrderQueryDTO;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.vo.FinanceOrderVO;

/**
 * 财务订单 Service
 */
public interface FinanceOrderService extends IService<FinanceOrder> {

    /**
     * 分页查询财务订单
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<FinanceOrderVO> page(FinanceOrderQueryDTO query);
}
