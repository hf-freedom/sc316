package com.aftersale.service;

import com.aftersale.entity.CompensationRecord;
import com.aftersale.entity.RiskControlUser;
import com.aftersale.store.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RiskControlService {

    @Value("${compensation.high-frequency-threshold:3}")
    private int highFrequencyThreshold;

    @Value("${compensation.high-frequency-days:7}")
    private int highFrequencyDays;

    public boolean isUserRestricted(String userId) {
        RiskControlUser user = DataStore.riskControlUsers.get(userId);
        return user != null && user.isRestricted() && user.getEndTime().isAfter(LocalDateTime.now());
    }

    public void checkAndAddHighFrequencyUser(String userId) {
        if (isUserRestricted(userId)) {
            return;
        }

        LocalDateTime startTime = LocalDateTime.now().minusDays(highFrequencyDays);
        int count = 0;

        for (CompensationRecord record : DataStore.compensationRecords.values()) {
            if (record.getUserId().equals(userId)
                    && record.getCreateTime().isAfter(startTime)
                    && !"REJECTED".equals(record.getStatus().name())
                    && !"REVOKED".equals(record.getStatus().name())
                    && !"ROLLBACK".equals(record.getStatus().name())) {
                count++;
            }
        }

        if (count >= highFrequencyThreshold) {
            RiskControlUser riskUser = new RiskControlUser();
            riskUser.setUserId(userId);
            riskUser.setCompensationCount(count);
            riskUser.setStartTime(LocalDateTime.now());
            riskUser.setEndTime(LocalDateTime.now().plusDays(30));
            riskUser.setRestricted(true);
            riskUser.setReason("高频赔付用户：" + highFrequencyDays + "天内赔付" + count + "次");
            riskUser.setCreateTime(LocalDateTime.now());
            DataStore.riskControlUsers.put(userId, riskUser);
        }
    }

    public List<RiskControlUser> getAllRestrictedUsers() {
        List<RiskControlUser> result = new ArrayList<>();
        for (Map.Entry<String, RiskControlUser> entry : DataStore.riskControlUsers.entrySet()) {
            if (entry.getValue().isRestricted()) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public void removeRestriction(String userId) {
        RiskControlUser user = DataStore.riskControlUsers.get(userId);
        if (user != null) {
            user.setRestricted(false);
            user.setEndTime(LocalDateTime.now());
        }
    }
}
