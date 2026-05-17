package com.aftersale.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCompensationStatVO {
    private String orderNo;
    private String userId;
    private BigDecimal totalCompensationAmount;
    private int compensationCount;
    private BigDecimal maxSingleAmount;
    private BigDecimal minSingleAmount;
    private BigDecimal avgAmount;
    private LocalDateTime firstCompensationTime;
    private LocalDateTime lastCompensationTime;
    private List<CompensationSimpleVO> compensationRecords;
    private boolean exceedLimit;
    private String limitTip;

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

    public BigDecimal getTotalCompensationAmount() {
        return totalCompensationAmount;
    }

    public void setTotalCompensationAmount(BigDecimal totalCompensationAmount) {
        this.totalCompensationAmount = totalCompensationAmount;
    }

    public int getCompensationCount() {
        return compensationCount;
    }

    public void setCompensationCount(int compensationCount) {
        this.compensationCount = compensationCount;
    }

    public BigDecimal getMaxSingleAmount() {
        return maxSingleAmount;
    }

    public void setMaxSingleAmount(BigDecimal maxSingleAmount) {
        this.maxSingleAmount = maxSingleAmount;
    }

    public BigDecimal getMinSingleAmount() {
        return minSingleAmount;
    }

    public void setMinSingleAmount(BigDecimal minSingleAmount) {
        this.minSingleAmount = minSingleAmount;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }

    public LocalDateTime getFirstCompensationTime() {
        return firstCompensationTime;
    }

    public void setFirstCompensationTime(LocalDateTime firstCompensationTime) {
        this.firstCompensationTime = firstCompensationTime;
    }

    public LocalDateTime getLastCompensationTime() {
        return lastCompensationTime;
    }

    public void setLastCompensationTime(LocalDateTime lastCompensationTime) {
        this.lastCompensationTime = lastCompensationTime;
    }

    public List<CompensationSimpleVO> getCompensationRecords() {
        return compensationRecords;
    }

    public void setCompensationRecords(List<CompensationSimpleVO> compensationRecords) {
        this.compensationRecords = compensationRecords;
    }

    public boolean isExceedLimit() {
        return exceedLimit;
    }

    public void setExceedLimit(boolean exceedLimit) {
        this.exceedLimit = exceedLimit;
    }

    public String getLimitTip() {
        return limitTip;
    }

    public void setLimitTip(String limitTip) {
        this.limitTip = limitTip;
    }

    public static class CompensationSimpleVO {
        private String id;
        private BigDecimal amount;
        private String status;
        private String statusDesc;
        private String problemType;
        private String problemTypeDesc;
        private LocalDateTime createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        public String getProblemType() {
            return problemType;
        }

        public void setProblemType(String problemType) {
            this.problemType = problemType;
        }

        public String getProblemTypeDesc() {
            return problemTypeDesc;
        }

        public void setProblemTypeDesc(String problemTypeDesc) {
            this.problemTypeDesc = problemTypeDesc;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }
}
