package com.aftersale.vo;

import com.aftersale.enums.CompensationStatus;
import com.aftersale.enums.CompensationType;
import com.aftersale.enums.ProblemType;
import com.aftersale.enums.ResponsibleParty;
import com.aftersale.enums.UserLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AfterSaleOrderVO {
    private String id;
    private String orderNo;
    private String userId;
    private String userName;
    private ProblemType problemType;
    private String problemTypeDesc;
    private ResponsibleParty responsibleParty;
    private String responsiblePartyDesc;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String compensationId;
    private BigDecimal orderAmount;
    private BigDecimal compensationAmount;
    private UserLevel userLevel;
    private String userLevelDesc;
    private CompensationStatus compensationStatus;
    private String compensationStatusDesc;
    private CompensationType compensationType;
    private String compensationTypeDesc;
    private String compensationReason;
    private String ruleMatchResult;
    private String calculationDetail;
    private boolean needApproval;
    private String approvalSuggestion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public String getProblemTypeDesc() {
        return problemTypeDesc;
    }

    public void setProblemTypeDesc(String problemTypeDesc) {
        this.problemTypeDesc = problemTypeDesc;
    }

    public ResponsibleParty getResponsibleParty() {
        return responsibleParty;
    }

    public void setResponsibleParty(ResponsibleParty responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public String getResponsiblePartyDesc() {
        return responsiblePartyDesc;
    }

    public void setResponsiblePartyDesc(String responsiblePartyDesc) {
        this.responsiblePartyDesc = responsiblePartyDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getCompensationId() {
        return compensationId;
    }

    public void setCompensationId(String compensationId) {
        this.compensationId = compensationId;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(BigDecimal compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelDesc() {
        return userLevelDesc;
    }

    public void setUserLevelDesc(String userLevelDesc) {
        this.userLevelDesc = userLevelDesc;
    }

    public CompensationStatus getCompensationStatus() {
        return compensationStatus;
    }

    public void setCompensationStatus(CompensationStatus compensationStatus) {
        this.compensationStatus = compensationStatus;
    }

    public String getCompensationStatusDesc() {
        return compensationStatusDesc;
    }

    public void setCompensationStatusDesc(String compensationStatusDesc) {
        this.compensationStatusDesc = compensationStatusDesc;
    }

    public CompensationType getCompensationType() {
        return compensationType;
    }

    public void setCompensationType(CompensationType compensationType) {
        this.compensationType = compensationType;
    }

    public String getCompensationTypeDesc() {
        return compensationTypeDesc;
    }

    public void setCompensationTypeDesc(String compensationTypeDesc) {
        this.compensationTypeDesc = compensationTypeDesc;
    }

    public String getCompensationReason() {
        return compensationReason;
    }

    public void setCompensationReason(String compensationReason) {
        this.compensationReason = compensationReason;
    }

    public String getRuleMatchResult() {
        return ruleMatchResult;
    }

    public void setRuleMatchResult(String ruleMatchResult) {
        this.ruleMatchResult = ruleMatchResult;
    }

    public String getCalculationDetail() {
        return calculationDetail;
    }

    public void setCalculationDetail(String calculationDetail) {
        this.calculationDetail = calculationDetail;
    }

    public boolean isNeedApproval() {
        return needApproval;
    }

    public void setNeedApproval(boolean needApproval) {
        this.needApproval = needApproval;
    }

    public String getApprovalSuggestion() {
        return approvalSuggestion;
    }

    public void setApprovalSuggestion(String approvalSuggestion) {
        this.approvalSuggestion = approvalSuggestion;
    }
}
