package com.aftersale.store;

import com.aftersale.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final ConcurrentHashMap<String, AfterSaleOrder> afterSaleOrders = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CompensationRecord> compensationRecords = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Coupon> coupons = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, BalanceRecord> balanceRecords = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, RiskControlUser> riskControlUsers = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, CompensationRule> compensationRules = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, UserInfo> users = new ConcurrentHashMap<>();

    public static List<CompensationRecord> getPendingApproval() {
        List<CompensationRecord> result = new ArrayList<>();
        for (CompensationRecord record : compensationRecords.values()) {
            if ("PENDING_APPROVAL".equals(record.getStatus().name())) {
                result.add(record);
            }
        }
        return result;
    }

    public static List<CompensationRecord> getPendingIssue() {
        List<CompensationRecord> result = new ArrayList<>();
        for (CompensationRecord record : compensationRecords.values()) {
            if ("PENDING_ISSUE".equals(record.getStatus().name())) {
                result.add(record);
            }
        }
        return result;
    }
}
