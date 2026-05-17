package com.aftersale.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BalanceRecord {
    private String id;
    private String userId;
    private String compensationRecordId;
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompensationRecordId() {
        return compensationRecordId;
    }

    public void setCompensationRecordId(String compensationRecordId) {
        this.compensationRecordId = compensationRecordId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public BigDecimal getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
