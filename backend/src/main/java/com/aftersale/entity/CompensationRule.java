package com.aftersale.entity;

import com.aftersale.enums.ProblemType;

import java.math.BigDecimal;

public class CompensationRule {
    private String id;
    private ProblemType problemType;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private double ratio;
    private boolean enabled;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
