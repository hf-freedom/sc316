package com.aftersale.service;

import com.aftersale.entity.CompensationRecord;
import com.aftersale.store.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private RiskControlService riskControlService;

    @Value("${compensation.approval-threshold:100}")
    private BigDecimal approvalThreshold;

    @Scheduled(cron = "${compensation.scheduled.cron:0 */5 * * * ?}")
    public void scanPendingApproval() {
        List<CompensationRecord> pending = DataStore.getPendingApproval();
        if (!pending.isEmpty()) {
            logger.info("扫描到 {} 条待审批赔付记录", pending.size());
            for (CompensationRecord record : pending) {
                logger.warn("待审批记录ID: {}, 金额: {}, 用户: {}",
                        record.getId(), record.getCompensationAmount(), record.getUserId());
            }
        }
    }

    @Scheduled(cron = "${compensation.scheduled.cron:0 */5 * * * ?}")
    public void scanPendingIssue() {
        List<CompensationRecord> pending = DataStore.getPendingIssue();
        if (!pending.isEmpty()) {
            logger.info("扫描到 {} 条待发放赔付记录", pending.size());
            for (CompensationRecord record : pending) {
                logger.warn("待发放记录ID: {}, 金额: {}, 用户: {}",
                        record.getId(), record.getCompensationAmount(), record.getUserId());
            }
        }
    }

    @Scheduled(cron = "${compensation.scheduled.cron:0 */5 * * * ?}")
    public void scanHighAmountCompensation() {
        BigDecimal highThreshold = approvalThreshold.multiply(new BigDecimal("3"));
        List<CompensationRecord> highAmount = new ArrayList<>();

        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getCompensationAmount().compareTo(highThreshold) >= 0
                    && record.getCreateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                highAmount.add(record);
            }
        }

        if (!highAmount.isEmpty()) {
            logger.info("扫描到 {} 条异常高额赔付记录", highAmount.size());
            for (CompensationRecord record : highAmount) {
                logger.error("异常高额赔付记录ID: {}, 金额: {}, 用户: {}, 状态: {}",
                        record.getId(), record.getCompensationAmount(),
                        record.getUserId(), record.getStatus().getDescription());
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyRiskControlCheck() {
        logger.info("执行每日风控检查");
        for (String userId : DataStore.users.keySet()) {
            riskControlService.checkAndAddHighFrequencyUser(userId);
        }
    }
}
