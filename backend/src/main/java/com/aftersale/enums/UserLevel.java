package com.aftersale.enums;

public enum UserLevel {
    NORMAL("普通用户", 1.0),
    SILVER("银牌用户", 1.1),
    GOLD("金牌用户", 1.2),
    PLATINUM("铂金用户", 1.3),
    DIAMOND("钻石用户", 1.5);

    private final String description;
    private final double ratio;

    UserLevel(String description, double ratio) {
        this.description = description;
        this.ratio = ratio;
    }

    public String getDescription() {
        return description;
    }

    public double getRatio() {
        return ratio;
    }
}
