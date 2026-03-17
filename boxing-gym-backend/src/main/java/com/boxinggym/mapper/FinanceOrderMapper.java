package com.boxinggym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxinggym.entity.FinanceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务订单 Mapper
 */
@Mapper
public interface FinanceOrderMapper extends BaseMapper<FinanceOrder> {
}
