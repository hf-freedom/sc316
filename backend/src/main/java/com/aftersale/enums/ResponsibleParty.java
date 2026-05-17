package com.aftersale.enums;

public enum ResponsibleParty {
    MERCHANT("商家责任", 1.0),
    PLATFORM("平台责任", 1.2),
    LOGISTICS("物流责任", 0.8),
    CUSTOMER("客户责任", 0.0);

    private final String description;
    private final double ratio;

    ResponsibleParty(String description, double ratio) {
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
