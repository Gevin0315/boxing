package com.boxinggym.task;

import com.boxinggym.service.MemberCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 会员卡状态检查定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CardStatusTask {

    private final MemberCardService memberCardService;

    /**
     * 检查并更新过期卡片状态
     * 每日凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAndUpdateExpiredCards() {
        log.info("开始执行过期卡片检查定时任务");
        try {
            memberCardService.checkAndUpdateExpiredCards();
            log.info("过期卡片检查定时任务执行完成");
        } catch (Exception e) {
            log.error("过期卡片检查定时任务执行失败", e);
        }
    }

    /**
     * 检查激活期限并作废超期未激活的卡
     * 每日凌晨1点30分执行
     */
    @Scheduled(cron = "0 30 1 * * ?")
    public void checkActivationDeadline() {
        log.info("开始执行激活期限检查定时任务");
        try {
            memberCardService.checkActivationDeadline();
            log.info("激活期限检查定时任务执行完成");
        } catch (Exception e) {
            log.error("激活期限检查定时任务执行失败", e);
        }
    }
}
