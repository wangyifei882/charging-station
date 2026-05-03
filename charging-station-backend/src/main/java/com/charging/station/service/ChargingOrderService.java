package com.charging.station.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.entity.ChargingOrder;
import com.charging.station.mapper.ChargingOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ChargingOrderService {
    @Autowired
    private ChargingOrderMapper chargingOrderMapper;

    public Page<ChargingOrder> getOrderPage(int page, int size, String orderNo, Integer status, Long deviceId) {
        Page<ChargingOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(ChargingOrder::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(ChargingOrder::getStatus, status);
        }
        if (deviceId != null) {
            wrapper.eq(ChargingOrder::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(ChargingOrder::getCreateTime);
        return chargingOrderMapper.selectPage(pageParam, wrapper);
    }

    public ChargingOrder getOrderById(Long id) {
        return chargingOrderMapper.selectById(id);
    }

    public void refund(Long id, BigDecimal refundAmount, String refundReason) {
        ChargingOrder order = new ChargingOrder();
        order.setId(id);
        order.setPaymentStatus(3);
        order.setRefundAmount(refundAmount);
        order.setRefundReason(refundReason);
        order.setUpdateTime(LocalDateTime.now());
        chargingOrderMapper.updateById(order);
    }

    public Object getRevenueStats(String timeDimension) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRevenue", 52380.00);
        stats.put("totalEnergy", 18560.50);
        stats.put("totalOrders", 1250);
        stats.put("avgOrderAmount", 41.90);
        return stats;
    }
}
