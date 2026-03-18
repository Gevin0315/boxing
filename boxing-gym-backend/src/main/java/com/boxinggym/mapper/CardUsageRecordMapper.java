package com.boxinggym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxinggym.entity.CardUsageRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员卡使用记录Mapper
 */
@Mapper
public interface CardUsageRecordMapper extends BaseMapper<CardUsageRecord> {

}
