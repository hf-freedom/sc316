package com.aftersale.enums;

public enum CompensationStatus {
    PENDING_APPROVAL("待审批"),
    APPROVED("已通过"),
    REJECTED("已拒绝"),
    PENDING_ISSUE("待发放"),
    ISSUED("已发放"),
    REVOKED("已撤销"),
    ROLLBACK("已回滚");

    private final String description;

    CompensationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
