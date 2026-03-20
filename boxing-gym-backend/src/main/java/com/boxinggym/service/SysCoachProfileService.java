package com.boxinggym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boxinggym.dto.CoachQueryDTO;
import com.boxinggym.entity.SysCoachProfile;
import com.boxinggym.vo.CoachVO;

/**
 * 教练扩展信息 Service
 */
public interface SysCoachProfileService extends IService<SysCoachProfile> {

    /**
     * 分页查询教练
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<CoachVO> page(CoachQueryDTO query);
}
