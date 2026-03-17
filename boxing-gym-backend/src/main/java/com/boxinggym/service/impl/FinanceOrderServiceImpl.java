package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.FinanceOrder;
import com.boxinggym.mapper.FinanceOrderMapper;
import com.boxinggym.service.FinanceOrderService;
import org.springframework.stereotype.Service;

/**
 * 财务订单 Service 实现
 */
@Service
public class FinanceOrderServiceImpl extends ServiceImpl<FinanceOrderMapper, FinanceOrder> implements FinanceOrderService {
}
