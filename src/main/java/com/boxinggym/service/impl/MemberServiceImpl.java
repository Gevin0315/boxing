package com.boxinggym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boxinggym.entity.Member;
import com.boxinggym.mapper.MemberMapper;
import com.boxinggym.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * 会员 Service 实现
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
}
