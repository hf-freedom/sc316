package com.aftersale.service;

import com.aftersale.dto.AfterSaleOrderCreateDTO;
import com.aftersale.entity.AfterSaleOrder;
import com.aftersale.entity.CompensationRecord;
import com.aftersale.entity.UserInfo;
import com.aftersale.enums.CompensationStatus;
import com.aftersale.enums.UserLevel;
import com.aftersale.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AfterSaleOrderService {

    @Autowired
    private CompensationCalculationService calculationService;

    @Autowired
    private RiskControlService riskControlService;

    @Autowired
    private UserService userService;

    @Value("${compensation.approval-threshold:100}")
    private BigDecimal approvalThreshold;

    @Value("${compensation.max-daily-compensation:500}")
    private BigDecimal maxDailyCompensation;

    public AfterSaleOrder createAfterSaleOrder(AfterSaleOrderCreateDTO dto) {
        if (riskControlService.isUserRestricted(dto.getUserId())) {
            throw new RuntimeException("该用户处于风控限制中，无法申请赔付");
        }

        if (isDailyLimitExceeded(dto.getUserId())) {
            throw new RuntimeException("该用户今日赔付金额已超限");
        }

        AfterSaleOrder order = new AfterSaleOrder();
        order.setId(UUID.randomUUID().toString());
        order.setOrderNo(dto.getOrderNo());
        order.setUserId(dto.getUserId());
        order.setProblemType(dto.getProblemType());
        order.setResponsibleParty(dto.getResponsibleParty());
        order.setDescription(dto.getDescription());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        DataStore.afterSaleOrders.put(order.getId(), order);

        createCompensationRecord(order, dto.getOrderAmount());

        return order;
    }

    private void createCompensationRecord(AfterSaleOrder order, BigDecimal orderAmount) {
        BigDecimal compensationAmount = calculationService.calculateCompensation(
                order.getUserId(),
                orderAmount,
                order.getProblemType(),
                order.getResponsibleParty()
        );

        if (compensationAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        CompensationRecord record = new CompensationRecord();
        record.setId(UUID.randomUUID().toString());
        record.setAfterSaleOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setUserId(order.getUserId());

        UserInfo user = userService.getUserById(order.getUserId());
        UserLevel userLevel = user != null ? user.getLevel() : UserLevel.NORMAL;
        record.setUserLevel(userLevel);

        record.setOrderAmount(orderAmount);
        record.setCompensationAmount(compensationAmount);

        if (compensationAmount.compareTo(approvalThreshold) > 0) {
            record.setStatus(CompensationStatus.PENDING_APPROVAL);
        } else {
            record.setStatus(CompensationStatus.PENDING_ISSUE);
        }

        record.setReason(order.getProblemType().getDescription());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        DataStore.compensationRecords.put(record.getId(), record);

        riskControlService.checkAndAddHighFrequencyUser(order.getUserId());
    }

    private boolean isDailyLimitExceeded(String userId) {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        BigDecimal total = BigDecimal.ZERO;

        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getUserId().equals(userId)
                    && record.getCreateTime().isAfter(todayStart)
                    && !"REJECTED".equals(record.getStatus().name())
                    && !"REVOKED".equals(record.getStatus().name())
                    && !"ROLLBACK".equals(record.getStatus().name())) {
                total = total.add(record.getCompensationAmount());
            }
        }

        return total.compareTo(maxDailyCompensation) >= 0;
    }

    public AfterSaleOrder getOrderById(String id) {
        return DataStore.afterSaleOrders.get(id);
    }

    public List<AfterSaleOrder> getAllOrders() {
        return new ArrayList<>(DataStore.afterSaleOrders.values());
    }

    public List<com.aftersale.vo.AfterSaleOrderVO> getAllOrdersWithCompensation() {
        List<com.aftersale.vo.AfterSaleOrderVO> result = new ArrayList<>();
        for (AfterSaleOrder order : DataStore.afterSaleOrders.values()) {
            result.add(convertToVO(order));
        }
        return result;
    }

    private com.aftersale.vo.AfterSaleOrderVO convertToVO(AfterSaleOrder order) {
        com.aftersale.vo.AfterSaleOrderVO vo = new com.aftersale.vo.AfterSaleOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setProblemType(order.getProblemType());
        vo.setProblemTypeDesc(order.getProblemType().getDescription());
        vo.setResponsibleParty(order.getResponsibleParty());
        vo.setResponsiblePartyDesc(order.getResponsibleParty().getDescription());
        vo.setDescription(order.getDescription());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        UserInfo user = userService.getUserById(order.getUserId());
        if (user != null) {
            vo.setUserName(user.getUserName());
        }

        CompensationRecord compensationRecord = null;
        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getAfterSaleOrderId().equals(order.getId())) {
                compensationRecord = record;
                break;
            }
        }

        if (compensationRecord != null) {
            vo.setCompensationId(compensationRecord.getId());
            vo.setOrderAmount(compensationRecord.getOrderAmount());
            vo.setCompensationAmount(compensationRecord.getCompensationAmount());
            vo.setUserLevel(compensationRecord.getUserLevel());
            vo.setUserLevelDesc(compensationRecord.getUserLevel().getDescription());
            vo.setCompensationStatus(compensationRecord.getStatus());
            vo.setCompensationStatusDesc(compensationRecord.getStatus().getDescription());
            vo.setCompensationReason(compensationRecord.getReason());

            if (compensationRecord.getCompensationType() != null) {
                vo.setCompensationType(compensationRecord.getCompensationType());
                vo.setCompensationTypeDesc(compensationRecord.getCompensationType().getDescription());
            }

            boolean needApproval = compensationRecord.getCompensationAmount().compareTo(approvalThreshold) > 0;
            vo.setNeedApproval(needApproval);

            StringBuilder ruleMatch = new StringBuilder();
            ruleMatch.append("匹配规则：").append(order.getProblemType().getDescription());
            ruleMatch.append("，基础比例：").append(order.getProblemType().getBaseRatio() * 100).append("%");
            vo.setRuleMatchResult(ruleMatch.toString());

            StringBuilder calcDetail = new StringBuilder();
            calcDetail.append("订单金额：¥").append(compensationRecord.getOrderAmount());
            calcDetail.append(" × 问题类型比例：").append(order.getProblemType().getBaseRatio());
            calcDetail.append(" × 用户等级系数：").append(compensationRecord.getUserLevel().getRatio());
            calcDetail.append(" × 责任方系数：").append(order.getResponsibleParty().getRatio());
            calcDetail.append(" = ¥").append(compensationRecord.getCompensationAmount());
            vo.setCalculationDetail(calcDetail.toString());

            if (needApproval) {
                vo.setApprovalSuggestion("赔付金额 ¥" + compensationRecord.getCompensationAmount() + " 超过审批阈值 ¥" + approvalThreshold + "，需主管审批");
            } else {
                vo.setApprovalSuggestion("赔付金额 ¥" + compensationRecord.getCompensationAmount() + " 在阈值范围内，可直接发放");
            }
        } else {
            vo.setRuleMatchResult("未匹配到赔付规则或责任方为客户，无需赔付");
            vo.setCalculationDetail("-");
            vo.setNeedApproval(false);
            vo.setApprovalSuggestion("无需赔付");
        }

        return vo;
    }

    public boolean revokeAfterSaleOrder(String id) {
        AfterSaleOrder order = DataStore.afterSaleOrders.get(id);
        if (order == null) {
            return false;
        }

        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getAfterSaleOrderId().equals(id)) {
                rollbackCompensation(record);
            }
        }

        DataStore.afterSaleOrders.remove(id);
        return true;
    }

    private void rollbackCompensation(CompensationRecord record) {
        if ("ISSUED".equals(record.getStatus().name())) {
            if (record.getCouponId() != null) {
                DataStore.coupons.remove(record.getCouponId());
            } else {
                userService.updateUserBalance(record.getUserId(), record.getCompensationAmount().negate());
            }
        }
        record.setStatus(CompensationStatus.ROLLBACK);
        record.setUpdateTime(LocalDateTime.now());
    }
}
