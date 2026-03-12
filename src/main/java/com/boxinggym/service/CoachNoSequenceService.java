package com.boxinggym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.common.BusinessException;
import com.boxinggym.entity.CoachNoSequence;
import com.boxinggym.mapper.CoachNoSequenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 教练号序列服务 - 支持回收和复用
 */
@Service
public class CoachNoSequenceService extends ServiceImpl<CoachNoSequenceMapper, CoachNoSequence> {

    /**
     * 生成新教练号（优先使用回收的）
     */
    public String generateCoachNo() {
        // 1. 先查找未使用的号码（回收的）
        CoachNoSequence unused = lambdaQuery()
                .eq(CoachNoSequence::getIsUsed, 0)
                .orderByAsc(CoachNoSequence::getCoachNo)
                .one();

        if (unused != null) {
            // 标记为已使用
            unused.setIsUsed(1);
            unused.setUsedTime(LocalDateTime.now());
            updateById(unused);
            return unused.getCoachNo();
        }

        // 2. 没有可回收的号码，检查是否需要扩容
        CoachNoSequence maxSeq = lambdaQuery()
                .select(CoachNoSequence::getCoachNo)
                .orderByDesc(CoachNoSequence::getCoachNo)
                .last("LIMIT 1")
                .one();
        String maxCoachNo = (maxSeq != null) ? maxSeq.getCoachNo() : null;
        long maxSeqNo = (maxCoachNo != null) ?
                Long.parseLong(maxCoachNo.substring(1)) : 0L;

        if (maxSeqNo >= 20000) {
            throw new BusinessException("教练号已用完，请联系管理员扩容");
        }

        // 3. 查找下一个号码是否存在
        String nextNo = "C" + String.format("%06d", maxSeqNo + 1);
        boolean exists = lambdaQuery()
                .eq(CoachNoSequence::getCoachNo, nextNo)
                .exists();

        if (!exists) {
            // 不存在则插入
            CoachNoSequence newSeq = new CoachNoSequence();
            newSeq.setCoachNo(nextNo);
            newSeq.setIsUsed(1);
            newSeq.setUsedTime(LocalDateTime.now());
            save(newSeq);
            return nextNo;
        }

        // 4. 存在则继续查找下一个
        for (int i = (int) (maxSeqNo + 2); i <= 20000; i++) {
            String tempNo = "C" + String.format("%06d", i);
            if (!lambdaQuery().eq(CoachNoSequence::getCoachNo, tempNo).exists()) {
                CoachNoSequence newSeq = new CoachNoSequence();
                newSeq.setCoachNo(tempNo);
                newSeq.setIsUsed(1);
                newSeq.setUsedTime(LocalDateTime.now());
                save(newSeq);
                return tempNo;
            }
        }

        throw new BusinessException("教练号已用完，请联系管理员");
    }

    /**
     * 回收教练号（删除教练时调用）
     */
    @Transactional
    public void recycleCoachNo(String coachNo, Long userId) {
        CoachNoSequence seq = lambdaQuery()
                .eq(CoachNoSequence::getCoachNo, coachNo)
                .one();

        if (seq != null) {
            seq.setIsUsed(0);
            seq.setUsedTime(null);
            seq.setUserId(null);
            updateById(seq);
        }
    }
}
