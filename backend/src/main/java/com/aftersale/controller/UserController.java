package com.aftersale.controller;

import com.aftersale.common.Result;
import com.aftersale.entity.RiskControlUser;
import com.aftersale.entity.UserInfo;
import com.aftersale.service.RiskControlService;
import com.aftersale.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RiskControlService riskControlService;

    @GetMapping
    public Result<List<UserInfo>> getAll() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public Result<UserInfo> getById(@PathVariable String id) {
        UserInfo user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/risk")
    public Result<List<RiskControlUser>> getRiskUsers() {
        return Result.success(riskControlService.getAllRestrictedUsers());
    }

    @PostMapping("/risk/{userId}/remove")
    public Result<Void> removeRiskRestriction(@PathVariable String userId) {
        riskControlService.removeRestriction(userId);
        return Result.success();
    }
}
