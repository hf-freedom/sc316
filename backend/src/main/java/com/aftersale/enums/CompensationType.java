package com.aftersale.enums;

public enum CompensationType {
    COUPON("补偿券"),
    BALANCE("余额");

    private final String description;

    CompensationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
