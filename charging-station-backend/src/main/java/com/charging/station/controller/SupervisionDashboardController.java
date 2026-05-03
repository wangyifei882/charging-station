package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.service.SupervisionDashboardService;
import com.charging.station.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/supervision/dashboard")
public class SupervisionDashboardController {

    @Autowired
    private SupervisionDashboardService dashboardService;

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics(@RequestParam(required = false) String regionCode) {
        return Result.success(dashboardService.getDashboardMetrics(regionCode));
    }

    @GetMapping("/charge-discharge-trend")
    public Result<Map<String, Object>> getChargeDischargeTrend(
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false) String regionCode,
            @RequestParam(defaultValue = "day") String aggregate) {
        return Result.success(dashboardService.getChargeDischargeTrend(days, regionCode, aggregate));
    }

    @GetMapping("/demand-response-effect")
    public Result<Map<String, Object>> getDemandResponseEffect(
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false) String regionCode) {
        return Result.success(dashboardService.getDemandResponseEffect(days, regionCode));
    }

    @GetMapping("/alarms")
    public Result<?> getAlarms(
            @RequestParam(required = false) Integer level,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String regionCode) {
        return Result.success(alarmService.pageByCondition(1, limit, level, null, regionCode, null));
    }

    @GetMapping("/unread-alarm-count")
    public Result<Long> getUnreadAlarmCount(@RequestParam(required = false) Integer level) {
        return Result.success(alarmService.getUnreadAlarmCount(level));
    }
}
