package com.aftersale.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String id;
    private String userId;
    private String compensationRecordId;
    private BigDecimal amount;
    private boolean used;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getUseTime() {
        return useTime;
    }

    public void setUseTime(LocalDateTime useTime) {
        this.useTime = useTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
