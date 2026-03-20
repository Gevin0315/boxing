package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.TrainingRecordQueryDTO;
import com.boxinggym.entity.TrainingRecord;
import com.boxinggym.vo.TrainingRecordVO;

/**
 * 上课签到记录 Service
 */
public interface TrainingRecordService extends IService<TrainingRecord> {

    /**
     * 分页查询签到记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<TrainingRecordVO> page(TrainingRecordQueryDTO query);
}
