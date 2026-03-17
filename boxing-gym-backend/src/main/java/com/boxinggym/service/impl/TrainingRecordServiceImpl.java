package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.mapper.TrainingRecordMapper;
import com.boxinggym.service.TrainingRecordService;
import org.springframework.stereotype.Service;

/**
 * 上课签到记录 Service 实现
 */
@Service
public class TrainingRecordServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements TrainingRecordService {
}
