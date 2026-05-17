package com.aftersale.entity;

import com.aftersale.enums.ProblemType;
import com.aftersale.enums.ResponsibleParty;

import java.time.LocalDateTime;

public class AfterSaleOrder {
    private String id;
    private String orderNo;
    private String userId;
    private ProblemType problemType;
    private ResponsibleParty responsibleParty;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

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

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public ResponsibleParty getResponsibleParty() {
        return responsibleParty;
    }

    public void setResponsibleParty(ResponsibleParty responsibleParty) {
        this.responsibleParty = responsibleParty;
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
}
