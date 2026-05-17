package com.aftersale.controller;

import com.aftersale.common.Result;
import com.aftersale.entity.CompensationRule;
import com.aftersale.service.CompensationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private CompensationRuleService ruleService;

    @GetMapping
    public Result<List<CompensationRule>> getAll() {
        return Result.success(ruleService.getAllRules());
    }

    @PutMapping
    public Result<CompensationRule> update(@RequestBody CompensationRule rule) {
        return Result.success(ruleService.updateRule(rule));
    }
}
