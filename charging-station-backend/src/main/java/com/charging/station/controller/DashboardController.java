package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.dto.iot.IotDevice;
import com.charging.station.dto.iot.IotDeviceListResponse;
import com.charging.station.dto.iot.IotTelemetryData;
import com.charging.station.dto.iot.IotBatchTelemetryResponse;
import com.charging.station.service.IotPlatformClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    @Autowired(required = false)
    private IotPlatformClientService iotClientService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            if (iotClientService != null) {
                IotDeviceListResponse response = iotClientService.getDeviceList(1, 1000, null, null);
                if (response != null && response.getCode() == 200 && response.getData() != null) {
                    return Result.success(buildFromIotData(response));
                }
            }
        } catch (Exception e) {
            logger.warn("从物联网平台获取数据失败，使用模拟数据", e);
        }
        
        return Result.success(buildMockData());
    }
    
    private Map<String, Object> buildFromIotData(IotDeviceListResponse response) {
        Map<String, Object> data = new HashMap<>();
        List<IotDevice> devices = response.getData().getDevices();
        
        Map<String, Object> deviceStats = new HashMap<>();
        int total = response.getData().getTotal() != null ? response.getData().getTotal() : devices.size();
        int onlineCount = (int) devices.stream()
                .filter(d -> "online".equals(d.getStatus()) || "idle".equals(d.getStatus()) || "charging".equals(d.getStatus()))
                .count();
        int offlineCount = (int) devices.stream().filter(d -> "offline".equals(d.getStatus())).count();
        int faultCount = (int) devices.stream().filter(d -> "fault".equals(d.getStatus())).count();
        
        deviceStats.put("total", total);
        deviceStats.put("online", onlineCount);
        deviceStats.put("offline", offlineCount);
        deviceStats.put("fault", faultCount);
        data.put("deviceStats", deviceStats);
        
        Map<String, Object> realtimeData = new HashMap<>();
        int idleCount = (int) devices.stream().filter(d -> "idle".equals(d.getStatus())).count();
        int chargingCount = (int) devices.stream().filter(d -> "charging".equals(d.getStatus())).count();
        
        double currentLoad = calculateCurrentLoad(devices);
        double todayEnergy = 1250.50;
        double todayRevenue = 3680.00;
        
        realtimeData.put("idleCount", idleCount);
        realtimeData.put("chargingCount", chargingCount);
        realtimeData.put("todayEnergy", todayEnergy);
        realtimeData.put("todayRevenue", todayRevenue);
        realtimeData.put("currentLoad", currentLoad);
        data.put("realtimeData", realtimeData);
        
        Map<String, Object> peakHours = new HashMap<>();
        peakHours.put("peakPeriod", "10:00-12:00");
        peakHours.put("currentPriceType", "峰时段");
        List<String> recommended = new ArrayList<>();
        recommended.add("AC-001");
        recommended.add("DC-003");
        peakHours.put("recommendedDevices", recommended);
        data.put("peakHours", peakHours);
        
        List<Map<String, Object>> recentAlarms = new ArrayList<>();
        data.put("recentAlarms", recentAlarms);
        
        return data;
    }
    
    private double calculateCurrentLoad(List<IotDevice> devices) {
        if (iotClientService == null || devices.isEmpty()) return 450.20;
        
        try {
            List<String> deviceIds = devices.stream()
                    .map(IotDevice::getIotDeviceId)
                    .limit(50)
                    .collect(Collectors.toList());
            
            IotBatchTelemetryResponse batchResponse = iotClientService.batchGetDeviceTelemetry(deviceIds);
            if (batchResponse != null && batchResponse.getCode() == 200 && batchResponse.getData() != null) {
                double totalLoad = 0;
                for (IotTelemetryData telemetry : batchResponse.getData()) {
                    if (telemetry.getTelemetry() != null && telemetry.getTelemetry().get("power") != null) {
                        totalLoad += Double.parseDouble(telemetry.getTelemetry().get("power").toString());
                    }
                }
                return totalLoad;
            }
        } catch (Exception e) {
            logger.warn("计算负载失败", e);
        }
        
        return 450.20;
    }
    
    private Map<String, Object> buildMockData() {
        Map<String, Object> data = new HashMap<>();
        
        Map<String, Object> deviceStats = new HashMap<>();
        deviceStats.put("total", 50);
        deviceStats.put("online", 45);
        deviceStats.put("offline", 3);
        deviceStats.put("fault", 2);
        data.put("deviceStats", deviceStats);
        
        Map<String, Object> realtimeData = new HashMap<>();
        realtimeData.put("idleCount", 20);
        realtimeData.put("chargingCount", 25);
        realtimeData.put("todayEnergy", 1250.50);
        realtimeData.put("todayRevenue", 3680.00);
        realtimeData.put("currentLoad", 450.20);
        data.put("realtimeData", realtimeData);
        
        Map<String, Object> peakHours = new HashMap<>();
        peakHours.put("peakPeriod", "10:00-12:00");
        peakHours.put("currentPriceType", "峰时段");
        List<String> recommended = new ArrayList<>();
        recommended.add("AC-001");
        recommended.add("DC-003");
        peakHours.put("recommendedDevices", recommended);
        data.put("peakHours", peakHours);
        
        List<Map<String, Object>> recentAlarms = new ArrayList<>();
        data.put("recentAlarms", recentAlarms);
        
        return data;
    }

    @GetMapping("/load-trend")
    public Result<Map<String, Object>> loadTrend(@RequestParam(defaultValue = "24") int hours) {
        Map<String, Object> data = new HashMap<>();
        List<String> timestamps = new ArrayList<>();
        List<Double> loads = new ArrayList<>();
        for (int i = 0; i < hours; i++) {
            timestamps.add(String.format("%02d:00", i));
            loads.add(200.0 + Math.random() * 300);
        }
        data.put("timestamps", timestamps);
        data.put("loads", loads);
        return Result.success(data);
    }
}
