package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.entity.*;
import com.charging.station.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/supervision/v2g")
public class V2gDispatchController {

    @Autowired
    private V2gActivityService v2gActivityService;

    @Autowired
    private com.charging.station.service.V2gActivityParticipantService v2gActivityParticipantService;

    @Autowired
    private OrderedChargingStrategyService orderedChargingStrategyService;

    @Autowired
    private DemandResponseActivityService demandResponseActivityService;

    @Autowired
    private DispatchService dispatchService;

    @GetMapping("/activities")
    public Result<IPage<com.charging.station.dto.V2gActivityDTO>> getActivityList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String regionCode) {
        return Result.success(v2gActivityService.pageByCondition(page, size, status, regionCode));
    }

    @PostMapping("/activities")
    public Result<V2gActivity> createActivity(@RequestBody V2gActivity activity) {
        // 自动生成活动编号：V2G + 年月日 + 4位随机数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", (int)(Math.random() * 10000));
        activity.setActivityNo("V2G" + dateStr + randomStr);
        
        activity.setStatus(0); // 草稿状态
        activity.setParticipatingVehicleCount(0);
        activity.setActualDischargeEnergy(BigDecimal.ZERO);
        v2gActivityService.save(activity);
        return Result.success(activity);
    }

    @GetMapping("/activities/stats")
    public Result<Map<String, Object>> getActivityStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // 进行中活动数量
        long activeCount = v2gActivityService.lambdaQuery()
            .eq(V2gActivity::getStatus, 1)
            .count();
        stats.put("activeActivities", activeCount);
        
        // 获取进行中活动的ID列表
        java.util.List<V2gActivity> activeActivities = v2gActivityService.lambdaQuery()
            .eq(V2gActivity::getStatus, 1)
            .list();
        java.util.List<Long> activeActivityIds = activeActivities.stream()
            .map(V2gActivity::getId)
            .collect(java.util.stream.Collectors.toList());
        
        // 参与运营商数量（只统计进行中活动的参与者）
        long operatorCount = 0;
        if (!activeActivityIds.isEmpty()) {
            operatorCount = v2gActivityParticipantService.lambdaQuery()
                .in(com.charging.station.entity.V2gActivityParticipant::getActivityId, activeActivityIds)
                .list()
                .stream()
                .map(com.charging.station.entity.V2gActivityParticipant::getOperatorId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
        }
        stats.put("participatingOperators", operatorCount);
        
        // 响应总电量（只统计进行中活动的实际放电量）
        BigDecimal totalEnergy = activeActivities.stream()
            .map(V2gActivity::getActualDischargeEnergy)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalResponseEnergy", totalEnergy.doubleValue());
        
        // 参与设备数（只统计进行中活动的车辆数）
        int totalVehicles = activeActivities.stream()
            .map(V2gActivity::getParticipatingVehicleCount)
            .filter(java.util.Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();
        stats.put("participatingDevices", totalVehicles);
        
        return Result.success(stats);
    }

    @GetMapping("/activities/{id}")
    public Result<Map<String, Object>> getActivityDetail(@PathVariable Long id) {
        return Result.success(v2gActivityService.getActivityDetail(id));
    }

    @GetMapping("/ordered-charging")
    public Result<IPage<OrderedChargingStrategy>> getOrderedChargingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderedChargingStrategyService.pageByCondition(page, size, regionCode, operatorId, status));
    }

    @GetMapping("/demand-response")
    public Result<IPage<com.charging.station.dto.DemandResponseActivityDTO>> getDemandResponseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer responseType,
            @RequestParam(required = false) Integer status) {
        return Result.success(demandResponseActivityService.pageByCondition(page, size, responseType, status));
    }

    @PostMapping("/demand-response")
    public Result<com.charging.station.entity.DemandResponseActivity> createDemandResponse(
            @RequestBody com.charging.station.entity.DemandResponseActivity activity) {
        // 自动生成活动编号
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", (int)(Math.random() * 10000));
        activity.setActivityNo("DR" + dateStr + randomStr);
        
        activity.setStatus(0); // 草稿状态
        activity.setActualEnergy(BigDecimal.ZERO);
        activity.setCompletionRate(BigDecimal.ZERO);
        activity.setParticipatingOperatorCount(0);
        demandResponseActivityService.save(activity);
        return Result.success(activity);
    }

    @GetMapping("/demand-response/stats")
    public Result<Map<String, Object>> getDemandResponseStats() {
        return Result.success(demandResponseActivityService.getStats());
    }

    @GetMapping("/demand-response/operator-ranking")
    public Result<java.util.List<com.charging.station.dto.OperatorParticipationRankDTO>> getOperatorRanking() {
        return Result.success(demandResponseActivityService.getOperatorRanking());
    }

    @GetMapping("/demand-response/{id}")
    public Result<Map<String, Object>> getDemandResponseDetail(@PathVariable Long id) {
        return Result.success(demandResponseActivityService.getActivityDetail(id));
    }

    @GetMapping("/dispatch/overview")
    public Result<Map<String, Object>> getDispatchOverview(@RequestParam String taskNo) {
        return Result.success(dispatchService.getOverview(taskNo));
    }

    @GetMapping("/dispatch/region-power-allocation")
    public Result<Map<String, Object>> getRegionPowerAllocation(@RequestParam String taskNo) {
        return Result.success(dispatchService.getRegionPowerAllocation(taskNo));
    }

    @GetMapping("/dispatch/power-trend")
    public Result<Map<String, Object>> getPowerTrend(@RequestParam String taskNo, @RequestParam(defaultValue = "24") Integer hours) {
        return Result.success(dispatchService.getPowerTrend(taskNo, hours));
    }

    @GetMapping("/dispatch/tasks")
    public Result<IPage<DispatchTask>> getDispatchTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer dispatchType) {
        
        Page<DispatchTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DispatchTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(DispatchTask::getStatus, status);
        }
        if (dispatchType != null) {
            wrapper.eq(DispatchTask::getDispatchType, dispatchType);
        }
        wrapper.orderByDesc(DispatchTask::getCreateTime);
        return Result.success(dispatchService.page(pageParam, wrapper));
    }

    @GetMapping("/dispatch/stats")
    public Result<Map<String, Object>> getDispatchStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // 统计活跃任务数
        long activeCount = dispatchService.lambdaQuery()
            .eq(DispatchTask::getStatus, 1)
            .count();
        stats.put("activeTasks", activeCount);
        
        // 统计总任务数
        long totalCount = dispatchService.count();
        stats.put("totalTasks", totalCount);
        
        // 统计完成率
        long completedCount = dispatchService.lambdaQuery()
            .eq(DispatchTask::getStatus, 2)
            .count();
        double completionRate = totalCount > 0 ? (completedCount * 100.0 / totalCount) : 0;
        stats.put("completionRate", String.format("%.1f", completionRate));
        
        // 模拟功率数据
        stats.put("allocatedPower", 12000);
        stats.put("usedPower", 7800);
        stats.put("remainingPower", 4200);
        stats.put("peakReduction", 2350);
        stats.put("costSaving", 15600);
        stats.put("dispatchProgress", 65);
        
        return Result.success(stats);
    }

    @GetMapping("/dispatch/stations/{stationId}")
    public Result<Map<String, Object>> getStationPowerDetail(
            @RequestParam String taskNo,
            @PathVariable Long stationId) {
        return Result.success(dispatchService.getStationPowerDetail(taskNo, stationId));
    }

    @PostMapping("/dispatch/adjust-power")
    public Result<Void> adjustStationPower(
            @RequestParam String taskNo,
            @RequestParam Long stationId,
            @RequestParam BigDecimal newChargePower,
            @RequestParam BigDecimal newDischargePower) {
        dispatchService.adjustStationPower(taskNo, stationId, newChargePower, newDischargePower);
        return Result.success();
    }

    @PostMapping("/dispatch/issue-command")
    public Result<Void> issueDispatchCommand(@RequestParam String taskNo) {
        dispatchService.issueDispatchCommand(taskNo);
        return Result.success();
    }

    @GetMapping("/effect-stats")
    public Result<Map<String, Object>> getV2gEffectStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String regionCode) {
        
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // 总充电量
        stats.put("totalCharge", 456789.5);
        // 总放电量
        stats.put("totalDischarge", 321456.8);
        // 削峰填谷量
        stats.put("peakReduction", 78456.3);
        // 平抑负荷波动
        stats.put("fluctuationReduction", 45.2);
        // 经济效益
        stats.put("economicBenefit", 234567.8);
        // 碳减排量
        stats.put("carbonReduction", 34567.9);
        // 参与站点数
        stats.put("stationCount", 156);
        // 参与设备数
        stats.put("deviceCount", 1245);
        // 用户满意度
        stats.put("userSatisfaction", 94.5);
        // 近30天每日统计
        stats.put("dailyStats", generateDailyStats());
        // 区域分布统计
        stats.put("regionStats", generateRegionStats());
        
        return Result.success(stats);
    }

    private java.util.List<Map<String, Object>> generateDailyStats() {
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        String[] dates = {"2025-01-01", "2025-01-02", "2025-01-03", "2025-01-04", "2025-01-05"};
        double[] charges = {12567.8, 13456.7, 14567.8, 13789.5, 14234.6};
        double[] discharges = {8765.4, 9345.6, 10234.5, 9678.3, 9876.4};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("date", dates[i]);
            item.put("charge", charges[i]);
            item.put("discharge", discharges[i]);
            list.add(item);
        }
        return list;
    }

    private java.util.List<Map<String, Object>> generateRegionStats() {
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        String[] regions = {"440304", "440305", "440306", "440307", "440308"};
        String[] regionNames = {"福田区", "南山区", "宝安区", "龙岗区", "盐田区"};
        double[] charges = {145678.9, 156789.0, 134567.8, 123456.7, 89678.5};
        double[] discharges = {98765.4, 102345.6, 87654.3, 82345.6, 56789.0};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("regionCode", regions[i]);
            item.put("regionName", regionNames[i]);
            item.put("charge", charges[i]);
            item.put("discharge", discharges[i]);
            list.add(item);
        }
        return list;
    }
}
