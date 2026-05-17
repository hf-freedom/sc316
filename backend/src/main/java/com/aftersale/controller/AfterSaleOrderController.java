package com.aftersale.controller;

import com.aftersale.common.Result;
import com.aftersale.dto.AfterSaleOrderCreateDTO;
import com.aftersale.entity.AfterSaleOrder;
import com.aftersale.service.AfterSaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/aftersale")
public class AfterSaleOrderController {

    @Autowired
    private AfterSaleOrderService afterSaleOrderService;

    @PostMapping
    public Result<AfterSaleOrder> create(@RequestBody @Valid AfterSaleOrderCreateDTO dto) {
        try {
            AfterSaleOrder order = afterSaleOrderService.createAfterSaleOrder(dto);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<com.aftersale.vo.AfterSaleOrderVO>> getAll() {
        return Result.success(afterSaleOrderService.getAllOrdersWithCompensation());
    }

    @GetMapping("/{id}")
    public Result<AfterSaleOrder> getById(@PathVariable String id) {
        AfterSaleOrder order = afterSaleOrderService.getOrderById(id);
        if (order == null) {
            return Result.error("售后单不存在");
        }
        return Result.success(order);
    }

    @DeleteMapping("/{id}")
    public Result<Void> revoke(@PathVariable String id) {
        boolean success = afterSaleOrderService.revokeAfterSaleOrder(id);
        if (!success) {
            return Result.error("售后单不存在");
        }
        return Result.success();
    }
}
