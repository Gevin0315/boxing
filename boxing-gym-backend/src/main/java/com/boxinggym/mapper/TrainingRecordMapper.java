package com.boxinggym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxinggym.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 上课签到记录 Mapper
 */
@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {
}
