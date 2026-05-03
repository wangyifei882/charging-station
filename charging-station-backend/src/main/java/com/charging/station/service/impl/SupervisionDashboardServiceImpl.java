package com.charging.station.service.impl;

import com.charging.station.entity.ChargingOrder;
import com.charging.station.mapper.ChargingOrderMapper;
import com.charging.station.service.SupervisionDashboardService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class SupervisionDashboardServiceImpl implements SupervisionDashboardService {

    @Autowired
    private ChargingOrderMapper chargingOrderMapper;

    @Override
    public Map<String, Object> getDashboardMetrics(String regionCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("facilityAccess", Map.of(
            "stationCount", 45,
            "deviceCount", 520,
            "v2gDeviceCount", 85,
            "v2gRatio", 16.35
        ));
        result.put("todayCharging", Map.of(
            "energy", 12500.50,
            "yesterdayRatio", 5.2
        ));
        result.put("todayV2gDischarge", Map.of(
            "energy", 850.30,
            "vehicleCount", 120
        ));
        result.put("demandResponse", Map.of(
            "peakShaveEnergy", 2500.00,
            "valleyFillEnergy", 1800.00
        ));
        result.put("operatorAccess", Map.of(
            "operatorCount", 8,
            "newThisMonth", 1
        ));
        result.put("deviceOnlineRate", Map.of(
            "rate", 96.50,
            "yesterdayRatio", 0.8
        ));
        result.put("faultAlarm", Map.of(
            "pendingCount", 15,
            "severeCount", 3
        ));
        result.put("subsidyGrant", Map.of(
            "yearTotal", 2500000.00
        ));
        result.put("dispatchExecution", Map.of(
            "executionRate", 92.50,
            "overloadStationCount", 2
        ));
        return result;
    }

    @Override
    public Map<String, Object> getChargeDischargeTrend(Integer days, String regionCode, String aggregate) {
        Map<String, Object> result = new HashMap<>();
        
        // 根据 aggregate 参数确定查询的时间范围和数据粒度
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime;
        DateTimeFormatter formatter;
        List<String> timeSlots = new ArrayList<>();
        
        switch (aggregate) {
            case "day":
                // 日视图：当天0点到现在，按小时分组
                startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                // 生成从00:00到现在的小时时间槽
                int currentHour = endTime.getHour();
                for (int i = 0; i <= currentHour; i++) {
                    timeSlots.add(String.format("%02d:00", i));
                }
                break;
            case "week":
                // 周视图：本周周一到今天，按天分组
                LocalDate today = LocalDate.now();
                LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                startTime = LocalDateTime.of(monday, LocalTime.MIN);
                formatter = DateTimeFormatter.ofPattern("MM-dd");
                // 生成从周一到今天的时间槽
                for (LocalDate date = monday; !date.isAfter(today); date = date.plusDays(1)) {
                    timeSlots.add(date.format(formatter));
                }
                break;
            case "month":
                // 月视图：本月1号到今天，按天分组
                LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
                startTime = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);
                formatter = DateTimeFormatter.ofPattern("dd");
                // 生成从1号到今天的时间槽
                for (LocalDate date = firstDayOfMonth; !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
                    timeSlots.add(date.format(formatter));
                }
                break;
            default:
                // 默认日视图
                startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                currentHour = endTime.getHour();
                for (int i = 0; i <= currentHour; i++) {
                    timeSlots.add(String.format("%02d:00", i));
                }
        }
        
        // 查询数据库获取真实的充电订单数据
        QueryWrapper<ChargingOrder> wrapper = new QueryWrapper<>();
        wrapper.ge("start_time", startTime)
               .le("start_time", endTime)
               .eq("status", 2)  // 已完成的订单
               .orderByAsc("start_time");
        
        List<ChargingOrder> orders = chargingOrderMapper.selectList(wrapper);
        
        // 按时间分组统计充电量
        Map<String, BigDecimal> chargeEnergyMap = new LinkedHashMap<>();
        
        // 初始化时间槽
        for (String slot : timeSlots) {
            chargeEnergyMap.put(slot, BigDecimal.ZERO);
        }
        
        // 统计订单数据
        for (ChargingOrder order : orders) {
            if (order.getTotalEnergy() == null || order.getStartTime() == null) {
                continue;
            }
            
            String key;
            LocalDateTime orderTime = order.getStartTime();
            
            if ("day".equals(aggregate)) {
                // 按小时分组
                int hour = orderTime.getHour();
                key = String.format("%02d:00", hour);
            } else {
                // 按天分组
                key = orderTime.toLocalDate().format(formatter);
            }
            
            if (chargeEnergyMap.containsKey(key)) {
                BigDecimal currentEnergy = chargeEnergyMap.get(key);
                chargeEnergyMap.put(key, currentEnergy.add(order.getTotalEnergy()));
            }
        }
        
        // 构建返回数据
        List<String> timestamps = new ArrayList<>(chargeEnergyMap.keySet());
        List<Double> chargeEnergy = new ArrayList<>();
        List<Double> dischargeEnergy = new ArrayList<>();
        List<Double> netChargeEnergy = new ArrayList<>();
        
        for (String key : timestamps) {
            double charge = chargeEnergyMap.getOrDefault(key, BigDecimal.ZERO).doubleValue();
            // 放电数据暂时按充电量的 15% 模拟（实际应从V2G活动表获取）
            double discharge = charge * 0.15;
            double net = charge - discharge;
            
            chargeEnergy.add(charge);
            dischargeEnergy.add(discharge);
            netChargeEnergy.add(net);
        }
        
        result.put("timestamps", timestamps);
        result.put("chargeEnergy", chargeEnergy);
        result.put("dischargeEnergy", dischargeEnergy);
        result.put("netChargeEnergy", netChargeEnergy);
        
        return result;
    }

    @Override
    public Map<String, Object> getDemandResponseEffect(Integer days, String regionCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamps", List.of("2025-01-08", "2025-01-09", "2025-01-10"));
        result.put("operators", List.of(
            Map.of("name", "南网电动", "color", "#FF6B6B", "data", List.of(120.50, 135.20, 128.00)),
            Map.of("name", "特来电", "color", "#4ECDC4", "data", List.of(95.30, 88.10, 102.50))
        ));
        result.put("targetLine", List.of(200.00, 200.00, 200.00));
        result.put("completionRates", List.of(97.50, 102.30, 98.80));
        return result;
    }
}
