package com.charging.station.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.common.Result;
import com.charging.station.entity.ChargingOrder;
import com.charging.station.service.ChargingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/charging/orders")
public class ChargingOrderController {
    @Autowired
    private ChargingOrderService chargingOrderService;

    @GetMapping
    public Result<Page<ChargingOrder>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deviceId) {
        return Result.success(chargingOrderService.getOrderPage(page, size, orderNo, status, deviceId));
    }

    @GetMapping("/{id}")
    public Result<ChargingOrder> getById(@PathVariable Long id) {
        return Result.success(chargingOrderService.getOrderById(id));
    }

    @PostMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        chargingOrderService.refund(id,
                new BigDecimal(params.get("refundAmount").toString()),
                params.get("refundReason").toString());
        return Result.success();
    }

    @GetMapping("/revenue-stats")
    public Result<Object> revenueStats(@RequestParam(required = false) String timeDimension) {
        return Result.success(chargingOrderService.getRevenueStats(timeDimension));
    }
}
