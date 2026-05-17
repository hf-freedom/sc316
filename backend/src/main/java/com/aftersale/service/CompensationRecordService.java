package com.aftersale.service;

import com.aftersale.dto.ApprovalDTO;
import com.aftersale.dto.IssueDTO;
import com.aftersale.entity.AfterSaleOrder;
import com.aftersale.entity.BalanceRecord;
import com.aftersale.entity.CompensationRecord;
import com.aftersale.entity.Coupon;
import com.aftersale.enums.CompensationStatus;
import com.aftersale.enums.CompensationType;
import com.aftersale.enums.ProblemType;
import com.aftersale.store.DataStore;
import com.aftersale.vo.OrderCompensationStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompensationRecordService {

    @Autowired
    private UserService userService;

    @Value("${compensation.approval-threshold:100}")
    private BigDecimal approvalThreshold;

    public List<CompensationRecord> getAllRecords() {
        return new ArrayList<>(DataStore.compensationRecords.values());
    }

    public CompensationRecord getRecordById(String id) {
        return DataStore.compensationRecords.get(id);
    }

    public List<CompensationRecord> getPendingApprovalRecords() {
        return DataStore.getPendingApproval();
    }

    public List<CompensationRecord> getPendingIssueRecords() {
        return DataStore.getPendingIssue();
    }

    public List<CompensationRecord> getHighAmountRecords() {
        List<CompensationRecord> result = new ArrayList<>();
        BigDecimal highThreshold = approvalThreshold.multiply(new BigDecimal("3"));
        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getCompensationAmount().compareTo(highThreshold) >= 0) {
                result.add(record);
            }
        }
        return result;
    }

    public CompensationRecord approve(ApprovalDTO dto) {
        CompensationRecord record = DataStore.compensationRecords.get(dto.getCompensationId());
        if (record == null || !"PENDING_APPROVAL".equals(record.getStatus().name())) {
            throw new RuntimeException("赔付记录不存在或状态不正确");
        }

        if (dto.isApproved()) {
            record.setStatus(CompensationStatus.PENDING_ISSUE);
        } else {
            record.setStatus(CompensationStatus.REJECTED);
            record.setReason(dto.getRejectReason());
        }
        record.setApprover(dto.getApprover());
        record.setApproveTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        return record;
    }

    public CompensationRecord issue(IssueDTO dto) {
        CompensationRecord record = DataStore.compensationRecords.get(dto.getCompensationId());
        if (record == null || !"PENDING_ISSUE".equals(record.getStatus().name())) {
            throw new RuntimeException("赔付记录不存在或状态不正确");
        }

        record.setCompensationType(dto.getCompensationType());

        if (dto.getCompensationType() == CompensationType.COUPON) {
            Coupon coupon = new Coupon();
            coupon.setId(UUID.randomUUID().toString());
            coupon.setUserId(record.getUserId());
            coupon.setCompensationRecordId(record.getId());
            coupon.setAmount(record.getCompensationAmount());
            coupon.setUsed(false);
            coupon.setExpireTime(LocalDateTime.now().plusDays(30));
            coupon.setCreateTime(LocalDateTime.now());
            DataStore.coupons.put(coupon.getId(), coupon);
            record.setCouponId(coupon.getId());
        } else {
            BigDecimal beforeBalance = userService.getUserById(record.getUserId()).getBalance();
            userService.updateUserBalance(record.getUserId(), record.getCompensationAmount());
            BigDecimal afterBalance = userService.getUserById(record.getUserId()).getBalance();

            BalanceRecord balanceRecord = new BalanceRecord();
            balanceRecord.setId(UUID.randomUUID().toString());
            balanceRecord.setUserId(record.getUserId());
            balanceRecord.setCompensationRecordId(record.getId());
            balanceRecord.setAmount(record.getCompensationAmount());
            balanceRecord.setBeforeBalance(beforeBalance);
            balanceRecord.setAfterBalance(afterBalance);
            balanceRecord.setCreateTime(LocalDateTime.now());
            DataStore.balanceRecords.put(balanceRecord.getId(), balanceRecord);
        }

        record.setStatus(CompensationStatus.ISSUED);
        record.setIssueTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        return record;
    }

    public List<Coupon> getAllCoupons() {
        return new ArrayList<>(DataStore.coupons.values());
    }

    public List<BalanceRecord> getAllBalanceRecords() {
        return new ArrayList<>(DataStore.balanceRecords.values());
    }

    public List<OrderCompensationStatVO> getOrderCompensationStats() {
        Map<String, List<CompensationRecord>> orderGroup = DataStore.compensationRecords.values().stream()
                .collect(Collectors.groupingBy(CompensationRecord::getOrderNo));

        List<OrderCompensationStatVO> result = new ArrayList<>();

        for (Map.Entry<String, List<CompensationRecord>> entry : orderGroup.entrySet()) {
            String orderNo = entry.getKey();
            List<CompensationRecord> records = entry.getValue();

            OrderCompensationStatVO stat = new OrderCompensationStatVO();
            stat.setOrderNo(orderNo);

            if (!records.isEmpty()) {
                stat.setUserId(records.get(0).getUserId());
            }

            BigDecimal total = BigDecimal.ZERO;
            BigDecimal max = BigDecimal.ZERO;
            BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
            LocalDateTime first = null;
            LocalDateTime last = null;
            int validCount = 0;

            List<OrderCompensationStatVO.CompensationSimpleVO> simpleVOs = new ArrayList<>();

            for (CompensationRecord record : records) {
                if (!"REJECTED".equals(record.getStatus().name())
                        && !"REVOKED".equals(record.getStatus().name())
                        && !"ROLLBACK".equals(record.getStatus().name())) {
                    total = total.add(record.getCompensationAmount());
                    validCount++;

                    if (record.getCompensationAmount().compareTo(max) > 0) {
                        max = record.getCompensationAmount();
                    }
                    if (record.getCompensationAmount().compareTo(min) < 0) {
                        min = record.getCompensationAmount();
                    }

                    if (first == null || record.getCreateTime().isBefore(first)) {
                        first = record.getCreateTime();
                    }
                    if (last == null || record.getCreateTime().isAfter(last)) {
                        last = record.getCreateTime();
                    }
                }

                OrderCompensationStatVO.CompensationSimpleVO simpleVO = new OrderCompensationStatVO.CompensationSimpleVO();
                simpleVO.setId(record.getId());
                simpleVO.setAmount(record.getCompensationAmount());
                simpleVO.setStatus(record.getStatus().name());
                simpleVO.setStatusDesc(record.getStatus().getDescription());

                for (AfterSaleOrder order : DataStore.afterSaleOrders.values()) {
                    if (order.getId().equals(record.getAfterSaleOrderId())) {
                        simpleVO.setProblemType(order.getProblemType().name());
                        simpleVO.setProblemTypeDesc(order.getProblemType().getDescription());
                        break;
                    }
                }

                simpleVO.setCreateTime(record.getCreateTime());
                simpleVOs.add(simpleVO);
            }

            stat.setTotalCompensationAmount(total);
            stat.setCompensationCount(validCount);
            stat.setCompensationRecords(simpleVOs);

            if (validCount > 0) {
                stat.setMaxSingleAmount(max);
                stat.setMinSingleAmount(min);
                stat.setAvgAmount(total.divide(new BigDecimal(validCount), 2, RoundingMode.HALF_UP));
                stat.setFirstCompensationTime(first);
                stat.setLastCompensationTime(last);

                BigDecimal orderAmountLimit = records.get(0).getOrderAmount().multiply(new BigDecimal("2"));
                if (total.compareTo(orderAmountLimit) > 0) {
                    stat.setExceedLimit(true);
                    stat.setLimitTip("累计赔付金额 ¥" + total + " 已超过订单金额 ¥" + records.get(0).getOrderAmount() + " 的2倍，需重点关注");
                } else {
                    stat.setExceedLimit(false);
                    stat.setLimitTip("累计赔付金额在正常范围内");
                }
            } else {
                stat.setMaxSingleAmount(BigDecimal.ZERO);
                stat.setMinSingleAmount(BigDecimal.ZERO);
                stat.setAvgAmount(BigDecimal.ZERO);
                stat.setExceedLimit(false);
                stat.setLimitTip("无有效赔付记录");
            }

            result.add(stat);
        }

        result.sort((a, b) -> b.getTotalCompensationAmount().compareTo(a.getTotalCompensationAmount()));

        return result;
    }
}
