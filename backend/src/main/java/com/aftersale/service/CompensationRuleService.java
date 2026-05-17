package com.aftersale.service;

import com.aftersale.entity.CompensationRule;
import com.aftersale.enums.ProblemType;
import com.aftersale.store.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CompensationRuleService {

    @PostConstruct
    public void initRules() {
        for (ProblemType type : ProblemType.values()) {
            CompensationRule rule = new CompensationRule();
            rule.setId(UUID.randomUUID().toString());
            rule.setProblemType(type);
            rule.setMaxAmount(new BigDecimal("500"));
            rule.setMinAmount(new BigDecimal("10"));
            rule.setRatio(type.getBaseRatio());
            rule.setEnabled(true);
            rule.setDescription(type.getDescription() + "赔付规则");
            DataStore.compensationRules.put(rule.getId(), rule);
        }
    }

    public CompensationRule getRuleByProblemType(ProblemType problemType) {
        for (CompensationRule rule : DataStore.compensationRules.values()) {
            if (rule.getProblemType() == problemType && rule.isEnabled()) {
                return rule;
            }
        }
        return null;
    }

    public List<CompensationRule> getAllRules() {
        return new ArrayList<>(DataStore.compensationRules.values());
    }

    public CompensationRule updateRule(CompensationRule rule) {
        DataStore.compensationRules.put(rule.getId(), rule);
        return rule;
    }
}
