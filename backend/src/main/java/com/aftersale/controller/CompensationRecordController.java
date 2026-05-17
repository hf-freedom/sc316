package com.aftersale.controller;

import com.aftersale.common.Result;
import com.aftersale.dto.ApprovalDTO;
import com.aftersale.dto.IssueDTO;
import com.aftersale.entity.BalanceRecord;
import com.aftersale.entity.CompensationRecord;
import com.aftersale.entity.Coupon;
import com.aftersale.service.CompensationRecordService;
import com.aftersale.vo.OrderCompensationStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/compensation")
public class CompensationRecordController {

    @Autowired
    private CompensationRecordService compensationRecordService;

    @GetMapping
    public Result<List<CompensationRecord>> getAll() {
        return Result.success(compensationRecordService.getAllRecords());
    }

    @GetMapping("/{id}")
    public Result<CompensationRecord> getById(@PathVariable String id) {
        CompensationRecord record = compensationRecordService.getRecordById(id);
        if (record == null) {
            return Result.error("赔付记录不存在");
        }
        return Result.success(record);
    }

    @GetMapping("/pending-approval")
    public Result<List<CompensationRecord>> getPendingApproval() {
        return Result.success(compensationRecordService.getPendingApprovalRecords());
    }

    @GetMapping("/pending-issue")
    public Result<List<CompensationRecord>> getPendingIssue() {
        return Result.success(compensationRecordService.getPendingIssueRecords());
    }

    @GetMapping("/high-amount")
    public Result<List<CompensationRecord>> getHighAmount() {
        return Result.success(compensationRecordService.getHighAmountRecords());
    }

    @PostMapping("/approve")
    public Result<CompensationRecord> approve(@RequestBody @Valid ApprovalDTO dto) {
        try {
            CompensationRecord record = compensationRecordService.approve(dto);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/issue")
    public Result<CompensationRecord> issue(@RequestBody @Valid IssueDTO dto) {
        try {
            CompensationRecord record = compensationRecordService.issue(dto);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/coupons")
    public Result<List<Coupon>> getCoupons() {
        return Result.success(compensationRecordService.getAllCoupons());
    }

    @GetMapping("/balance-records")
    public Result<List<BalanceRecord>> getBalanceRecords() {
        return Result.success(compensationRecordService.getAllBalanceRecords());
    }

    @GetMapping("/order-stats")
    public Result<List<OrderCompensationStatVO>> getOrderStats() {
        return Result.success(compensationRecordService.getOrderCompensationStats());
    }
}
