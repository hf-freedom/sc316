package com.aftersale.dto;

import com.aftersale.enums.ProblemType;
import com.aftersale.enums.ResponsibleParty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AfterSaleOrderCreateDTO {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @NotNull(message = "问题类型不能为空")
    private ProblemType problemType;
    @NotNull(message = "责任方不能为空")
    private ResponsibleParty responsibleParty;
    private String description;
    @NotNull(message = "订单金额不能为空")
    private BigDecimal orderAmount;

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

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
}
