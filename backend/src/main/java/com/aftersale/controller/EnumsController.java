package com.aftersale.controller;

import com.aftersale.common.Result;
import com.aftersale.enums.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enums")
public class EnumsController {

    public static class EnumVO {
        private String name;
        private String value;

        public EnumVO(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    @GetMapping("/problem-types")
    public Result<List<EnumVO>> getProblemTypes() {
        List<EnumVO> result = Arrays.stream(ProblemType.values())
                .map(e -> new EnumVO(e.name(), e.getDescription()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/responsible-parties")
    public Result<List<EnumVO>> getResponsibleParties() {
        List<EnumVO> result = Arrays.stream(ResponsibleParty.values())
                .map(e -> new EnumVO(e.name(), e.getDescription()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/user-levels")
    public Result<List<EnumVO>> getUserLevels() {
        List<EnumVO> result = Arrays.stream(UserLevel.values())
                .map(e -> new EnumVO(e.name(), e.getDescription()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/compensation-statuses")
    public Result<List<EnumVO>> getCompensationStatuses() {
        List<EnumVO> result = Arrays.stream(CompensationStatus.values())
                .map(e -> new EnumVO(e.name(), e.getDescription()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/compensation-types")
    public Result<List<EnumVO>> getCompensationTypes() {
        List<EnumVO> result = Arrays.stream(CompensationType.values())
                .map(e -> new EnumVO(e.name(), e.getDescription()))
                .collect(Collectors.toList());
        return Result.success(result);
    }
}
