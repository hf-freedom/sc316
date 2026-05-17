package com.aftersale.entity;

import com.aftersale.enums.UserLevel;

import java.math.BigDecimal;

public class UserInfo {
    private String userId;
    private String userName;
    private UserLevel level;
    private BigDecimal balance;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserLevel getLevel() {
        return level;
    }

    public void setLevel(UserLevel level) {
        this.level = level;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
