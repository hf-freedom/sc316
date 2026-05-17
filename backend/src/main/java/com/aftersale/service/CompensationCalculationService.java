package com.aftersale.service;

import com.aftersale.entity.CompensationRule;
import com.aftersale.entity.UserInfo;
import com.aftersale.enums.ResponsibleParty;
import com.aftersale.enums.UserLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CompensationCalculationService {

    @Autowired
    private CompensationRuleService ruleService;

    @Autowired
    private UserService userService;

    public BigDecimal calculateCompensation(String userId, BigDecimal orderAmount,
                                            com.aftersale.enums.ProblemType problemType,
                                            ResponsibleParty responsibleParty) {
        if (responsibleParty == ResponsibleParty.CUSTOMER) {
            return BigDecimal.ZERO;
        }

        CompensationRule rule = ruleService.getRuleByProblemType(problemType);
        if (rule == null) {
            return BigDecimal.ZERO;
        }

        UserInfo user = userService.getUserById(userId);
        UserLevel userLevel = user != null ? user.getLevel() : UserLevel.NORMAL;

        BigDecimal baseAmount = orderAmount.multiply(BigDecimal.valueOf(rule.getRatio()));
        BigDecimal levelAdjusted = baseAmount.multiply(BigDecimal.valueOf(userLevel.getRatio()));
        BigDecimal finalAmount = levelAdjusted.multiply(BigDecimal.valueOf(responsibleParty.getRatio()));

        if (finalAmount.compareTo(rule.getMinAmount()) < 0) {
            finalAmount = rule.getMinAmount();
        }
        if (finalAmount.compareTo(rule.getMaxAmount()) > 0) {
            finalAmount = rule.getMaxAmount();
        }

        return finalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isOrderCumulativeLimitExceeded(String orderNo, BigDecimal newAmount) {
        return false;
    }
}
