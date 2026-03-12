package com.boxinggym.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.common.BusinessException;
import com.boxinggym.entity.MemberNoSequence;
import com.boxinggym.mapper.MemberNoSequenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 会员号序列服务 - 支持回收和复用
 */
@Service
public class MemberNoSequenceService extends ServiceImpl<MemberNoSequenceMapper, MemberNoSequence> {

    /**
     * 生成新会员号（优先使用回收的）
     */
    public String generateMemberNo() {
        // 1. 先查找未使用的号码（回收的）
        MemberNoSequence unused = lambdaQuery()
                .eq(MemberNoSequence::getIsUsed, 0)
                .orderByAsc(MemberNoSequence::getMemberNo)
                .one();

        if (unused != null) {
            // 标记为已使用
            unused.setIsUsed(1);
            unused.setUsedTime(LocalDateTime.now());
            updateById(unused);
            return unused.getMemberNo();
        }

        // 2. 没有可回收的号码，检查是否需要扩容
        MemberNoSequence maxSeq = lambdaQuery()
                .select(MemberNoSequence::getMemberNo)
                .orderByDesc(MemberNoSequence::getMemberNo)
                .last("LIMIT 1")
                .one();
        String maxMemberNo = (maxSeq != null) ? maxSeq.getMemberNo() : null;
        long maxSeqNo = (maxMemberNo != null) ?
                Long.parseLong(maxMemberNo.substring(1)) : 0L;

        if (maxSeqNo >= 100000) {
            throw new BusinessException("会员号已用完，请联系管理员扩容");
        }

        // 3. 查找下一个号码是否存在
        String nextNo = "M" + String.format("%06d", maxSeqNo + 1);
        boolean exists = lambdaQuery()
                .eq(MemberNoSequence::getMemberNo, nextNo)
                .exists();

        if (!exists) {
            // 不存在则插入
            MemberNoSequence newSeq = new MemberNoSequence();
            newSeq.setMemberNo(nextNo);
            newSeq.setIsUsed(1);
            newSeq.setUsedTime(LocalDateTime.now());
            save(newSeq);
            return nextNo;
        }

        // 4. 存在则继续查找下一个
        for (int i = (int) (maxSeqNo + 2); i <= 100000; i++) {
            String tempNo = "M" + String.format("%06d", i);
            if (!lambdaQuery().eq(MemberNoSequence::getMemberNo, tempNo).exists()) {
                MemberNoSequence newSeq = new MemberNoSequence();
                newSeq.setMemberNo(tempNo);
                newSeq.setIsUsed(1);
                newSeq.setUsedTime(LocalDateTime.now());
                save(newSeq);
                return tempNo;
            }
        }

        throw new BusinessException("会员号已用完，请联系管理员");
    }

    /**
     * 回收会员号（删除会员时调用）
     */
    @Transactional
    public void recycleMemberNo(String memberNo, Long memberId) {
        MemberNoSequence seq = lambdaQuery()
                .eq(MemberNoSequence::getMemberNo, memberNo)
                .one();

        if (seq != null) {
            seq.setIsUsed(0);
            seq.setUsedTime(null);
            seq.setMemberId(null);
            updateById(seq);
        }
    }
}
