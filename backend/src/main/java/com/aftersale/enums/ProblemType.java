package com.aftersale.enums;

public enum ProblemType {
    QUALITY_ISSUE("质量问题", 0.5),
    DELIVERY_DELAY("发货延迟", 0.3),
    WRONG_ITEM("发错商品", 0.8),
    DAMAGED("商品破损", 0.6),
    DESCRIPTION_MISMATCH("描述不符", 0.4),
    OTHER("其他问题", 0.2);

    private final String description;
    private final double baseRatio;

    ProblemType(String description, double baseRatio) {
        this.description = description;
        this.baseRatio = baseRatio;
    }

    public String getDescription() {
        return description;
    }

    public double getBaseRatio() {
        return baseRatio;
    }
}
