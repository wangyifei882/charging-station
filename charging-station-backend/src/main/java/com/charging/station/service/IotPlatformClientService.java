package com.charging.station.service;

import com.charging.station.config.IotPlatformConfig;
import com.charging.station.dto.iot.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class IotPlatformClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(IotPlatformClientService.class);
    
    @Autowired
    private IotPlatformConfig iotConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MqttClientService mqttClientService;
    
    // 备用模拟设备数据
    private final List<IotDevice> mockDevices;
    
    public IotPlatformClientService() {
        this.mockDevices = generateMockDevices();
    }
    
    @PostConstruct
    public void init() {
        logger.info("IotPlatformClientService 初始化完成，使用 MQTT 订阅方式获取数据");
    }
    
    // 直流桩额定功率选项：60kW, 120kW, 160kW, 180kW, 240kW, 360kW
    private static final int[] DC_POWER_RATINGS = {60, 120, 120, 160, 180, 240, 360};
    // 交流桩额定功率选项：3.5kW, 7kW, 11kW, 22kW
    private static final int[] AC_POWER_RATINGS = {3, 7, 7, 11, 22};
    
    private List<IotDevice> generateMockDevices() {
        List<IotDevice> devices = new ArrayList<>();
        
        String[] types = {"charger"};
        String[] areas = {"A区", "B区", "C区"};
        String[] manufacturers = {"特来电", "星星充电", "南方电网", "国家电网", "云快充"};
        String[] dcModels = {"TLD-DC-120", "SC-DC-60", "NG-DC-180", "TK-DC-240", "EV-DC-360"};
        String[] acModels = {"TLD-AC-7", "SC-AC-11", "NG-AC-22", "TK-AC-7", "EV-AC-3"};
        
        for (int i = 1; i <= 20; i++) {
            IotDevice device = new IotDevice();
            // 交替生成交直流桩，但让分布更随机
            String type = (i % 3 == 0) ? "ac" : "dc"; // 约1/3交流桩，2/3直流桩
            String deviceCode = type.equals("dc") ? "DC-" + String.format("%03d", i) : "AC-" + String.format("%03d", i);
            
            device.setIotDeviceId(deviceCode);
            device.setDeviceName((type.equals("dc") ? "直流充电桩" : "交流充电桩") + i + "号");
            device.setDeviceType(types[0]);
            device.setSubType(type);
            
            // 状态分布：40%充电中, 35%空闲, 15%离线, 10%故障
            int statusRand = i % 20;
            String status;
            if (statusRand < 8) {
                status = "charging";
            } else if (statusRand < 15) {
                status = "idle";
            } else if (statusRand < 18) {
                status = "offline";
            } else {
                status = "fault";
            }
            device.setStatus(status);
            
            IotDevice.Location location = new IotDevice.Location();
            location.setArea(areas[i % 3]);
            location.setPosition(areas[i % 3] + "-" + String.format("%02d", (i % 10) + 1));
            device.setLocation(location);
            
            // 根据类型选择额定功率和型号
            int powerRating;
            String model;
            if (type.equals("dc")) {
                powerRating = DC_POWER_RATINGS[i % DC_POWER_RATINGS.length];
                model = dcModels[i % dcModels.length];
            } else {
                powerRating = AC_POWER_RATINGS[i % AC_POWER_RATINGS.length];
                model = acModels[i % acModels.length];
            }
            
            // 计算实时功率
            double realTimePower = calculateRealTimePower(status, powerRating);
            
            Map<String, Object> attrs = new HashMap<>();
            attrs.put("manufacturer", manufacturers[i % manufacturers.length]);
            attrs.put("model", model);
            attrs.put("powerRating", powerRating);
            attrs.put("realTimePower", realTimePower);
            attrs.put("firmwareVersion", "v" + (2 + i % 3) + "." + (i % 10) + "." + (i % 5));
            device.setAttributes(attrs);
            
            devices.add(device);
        }
        
        return devices;
    }
    
    /**
     * 根据设备状态和额定功率计算实时功率
     */
    private double calculateRealTimePower(String status, int powerRating) {
        switch (status) {
            case "charging":
                // 充电中：实时功率为额定功率的 30% - 95%，模拟不同充电阶段
                double loadFactor = 0.3 + Math.random() * 0.65;
                return Math.round(powerRating * loadFactor * 10.0) / 10.0;
            case "idle":
            case "offline":
            case "fault":
                // 空闲/离线/故障：待机功耗 0.1 - 0.5 kW
                return Math.round((0.1 + Math.random() * 0.4) * 10.0) / 10.0;
            default:
                return 0.0;
        }
    }
    
    /**
     * 获取设备列表 - 从 MQTT 缓存数据或模拟数据
     */
    public IotDeviceListResponse getDeviceList(Integer page, Integer size, String deviceType, String status) {
        logger.info("正在从 MQTT 数据获取设备列表...");
        
        // 尝试从 MQTT 获取设备状态
        Map<String, String> mqttStatuses = mqttClientService.getAllDeviceStatus();
        Map<String, Map<String, Object>> mqttData = mqttClientService.getAllDeviceRealTimeData();
        
        List<IotDevice> deviceList = new ArrayList<>();
        
        // 如果有 MQTT 数据，优先使用
        if (!mqttData.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : mqttData.entrySet()) {
                String nodeId = entry.getKey();
                Map<String, Object> data = entry.getValue();
                
                IotDevice device = new IotDevice();
                device.setIotDeviceId(nodeId);
                device.setDeviceName("充电桩-" + nodeId);
                device.setDeviceType("charger");
                device.setStatus(mqttStatuses.getOrDefault(nodeId, "online"));
                
                Map<String, Object> attrs = new HashMap<>();
                attrs.putAll(data);
                device.setAttributes(attrs);
                
                deviceList.add(device);
            }
        }
        
        // 如果 MQTT 没有数据，使用模拟数据并动态计算实时功率
        if (deviceList.isEmpty()) {
            logger.info("MQTT 无数据，使用模拟设备数据");
            deviceList = new ArrayList<>();
            
            for (IotDevice mockDevice : mockDevices) {
                IotDevice device = new IotDevice();
                device.setIotDeviceId(mockDevice.getIotDeviceId());
                device.setDeviceName(mockDevice.getDeviceName());
                device.setDeviceType(mockDevice.getDeviceType());
                device.setSubType(mockDevice.getSubType());
                device.setStatus(mockDevice.getStatus());
                device.setLocation(mockDevice.getLocation());
                
                // 复制属性并重新计算实时功率
                Map<String, Object> attrs = new HashMap<>(mockDevice.getAttributes());
                int powerRating = (int) attrs.getOrDefault("powerRating", 60);
                double realTimePower = calculateRealTimePower(mockDevice.getStatus(), powerRating);
                attrs.put("realTimePower", realTimePower);
                device.setAttributes(attrs);
                
                deviceList.add(device);
            }
        }
        
        // 过滤和分页
        if (status != null && !status.isEmpty()) {
            deviceList = deviceList.stream()
                    .filter(d -> status.equalsIgnoreCase(d.getStatus()))
                    .toList();
        }
        
        IotDeviceListResponse response = new IotDeviceListResponse();
        response.setCode(200);
        response.setMessage("success");
        
        IotDeviceListResponse.DeviceListData data = new IotDeviceListResponse.DeviceListData();
        data.setTotal(deviceList.size());
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, deviceList.size());
        if (start < deviceList.size()) {
            data.setDevices(deviceList.subList(start, end));
        } else {
            data.setDevices(new ArrayList<>());
        }
        
        response.setData(data);
        return response;
    }
    
    /**
     * 获取设备遥测数据 - 从 MQTT 缓存
     */
    public IotTelemetryResponse getDeviceTelemetry(String iotDeviceId) {
        logger.info("正在从 MQTT 获取设备 {} 的遥测数据", iotDeviceId);
        
        Map<String, Object> mqttData = mqttClientService.getDeviceRealTimeData(iotDeviceId);
        
        IotTelemetryResponse response = new IotTelemetryResponse();
        response.setCode(200);
        response.setMessage("success");
        
        IotTelemetryData data = new IotTelemetryData();
        data.setIotDeviceId(iotDeviceId);
        data.setTimestamp(new Date().toString());
        
        if (mqttData != null && !mqttData.isEmpty()) {
            // 使用 MQTT 数据
            data.setTelemetry(mqttData);
            data.setQuality("good");
            logger.info("从 MQTT 获取到设备 {} 的遥测数据", iotDeviceId);
        } else {
            // 生成模拟数据
            logger.info("MQTT 无数据，使用模拟遥测数据");
            data.setTelemetry(generateMockTelemetry(iotDeviceId));
            data.setQuality("simulated");
        }
        
        response.setData(data);
        return response;
    }
    
    /**
     * 批量获取设备遥测数据
     */
    public IotBatchTelemetryResponse batchGetDeviceTelemetry(List<String> deviceIds) {
        logger.info("批量从 MQTT 获取 {} 个设备的遥测数据", deviceIds.size());
        
        IotBatchTelemetryResponse response = new IotBatchTelemetryResponse();
        response.setCode(200);
        response.setMessage("success");
        
        List<IotTelemetryData> telemetryList = new ArrayList<>();
        
        for (String deviceId : deviceIds) {
            IotTelemetryResponse singleResponse = getDeviceTelemetry(deviceId);
            if (singleResponse.getData() != null) {
                telemetryList.add(singleResponse.getData());
            }
        }
        
        response.setData(telemetryList);
        return response;
    }
    
    /**
     * 生成模拟遥测数据 - 根据设备类型和状态生成真实数据
     */
    private Map<String, Object> generateMockTelemetry(String iotDeviceId) {
        Map<String, Object> telemetry = new HashMap<>();
        
        boolean isDc = iotDeviceId.startsWith("DC");
        int deviceIndex = Integer.parseInt(iotDeviceId.substring(3)) - 1;
        
        // 获取额定功率
        int powerRating = isDc 
            ? DC_POWER_RATINGS[deviceIndex % DC_POWER_RATINGS.length]
            : AC_POWER_RATINGS[deviceIndex % AC_POWER_RATINGS.length];
        
        // 模拟状态分布：40%充电中, 35%空闲, 15%离线, 10%故障
        int statusRand = deviceIndex % 20;
        String status;
        if (statusRand < 8) {
            status = "charging";
        } else if (statusRand < 15) {
            status = "idle";
        } else if (statusRand < 18) {
            status = "offline";
        } else {
            status = "fault";
        }
        
        if (isDc) {
            // 直流桩数据
            telemetry.put("status", status);
            
            if ("charging".equals(status)) {
                // 充电中：有真实的功率、电压、电流
                double loadFactor = 0.35 + Math.random() * 0.6; // 35% - 95% 负载
                double realTimePower = Math.round(powerRating * loadFactor * 10.0) / 10.0;
                double voltage = 350 + Math.random() * 100; // 350V - 450V
                double current = Math.round(realTimePower * 1000 / voltage * 10.0) / 10.0;
                
                telemetry.put("ChargeVoltage", Math.round(voltage * 10.0) / 10.0);
                telemetry.put("ChargeCurrent", current);
                telemetry.put("ActivePower", realTimePower);
                telemetry.put("RealTimePower", realTimePower);
                telemetry.put("DischargePower", 0.0);
                telemetry.put("ReactivePower", Math.round(realTimePower * 0.05 * 10.0) / 10.0); // 约5%无功
                telemetry.put("ChargeDuration", Math.round(10 + Math.random() * 120)); // 10-130分钟
                telemetry.put("CumulativeEnergy", Math.round(50 + Math.random() * 800));
                telemetry.put("SOC", Math.round(20 + Math.random() * 60)); // 20% - 80%
                telemetry.put("temperature", Math.round(25 + Math.random() * 35)); // 25°C - 60°C
            } else {
                // 空闲/离线/故障：待机状态
                telemetry.put("ChargeVoltage", 0.0);
                telemetry.put("ChargeCurrent", 0.0);
                telemetry.put("ActivePower", Math.round((0.1 + Math.random() * 0.3) * 10.0) / 10.0); // 待机功耗 0.1-0.4kW
                telemetry.put("RealTimePower", 0.0);
                telemetry.put("DischargePower", 0.0);
                telemetry.put("ReactivePower", 0.0);
                telemetry.put("ChargeDuration", 0.0);
                telemetry.put("CumulativeEnergy", Math.round(Math.random() * 1000));
                telemetry.put("SOC", 0.0);
                telemetry.put("temperature", Math.round(15 + Math.random() * 20)); // 15°C - 35°C（待机温度较低）
            }
        } else {
            // 交流桩数据
            telemetry.put("status", status);
            
            if ("charging".equals(status)) {
                // 充电中
                double loadFactor = 0.4 + Math.random() * 0.55; // 40% - 95% 负载
                double realTimePower = Math.round(powerRating * loadFactor * 10.0) / 10.0;
                double voltage = 220 + Math.random() * 20; // 220V - 240V
                double current = Math.round(realTimePower * 1000 / voltage * 10.0) / 10.0;
                
                telemetry.put("ChargeVoltage", Math.round(voltage * 10.0) / 10.0);
                telemetry.put("ChargeCurrent", current);
                telemetry.put("ActivePower", realTimePower);
                telemetry.put("RealTimePower", realTimePower);
                telemetry.put("DischargePower", 0.0);
                telemetry.put("ReactivePower", Math.round(realTimePower * 0.03 * 10.0) / 10.0); // 约3%无功
                telemetry.put("ChargeDuration", Math.round(30 + Math.random() * 300)); // 30-330分钟
                telemetry.put("CumulativeEnergy", Math.round(20 + Math.random() * 300));
                telemetry.put("SOC", Math.round(30 + Math.random() * 50)); // 30% - 80%
                telemetry.put("temperature", Math.round(20 + Math.random() * 25)); // 20°C - 45°C
            } else {
                // 空闲/离线/故障
                telemetry.put("ChargeVoltage", 0.0);
                telemetry.put("ChargeCurrent", 0.0);
                telemetry.put("ActivePower", Math.round((0.05 + Math.random() * 0.15) * 10.0) / 10.0); // 待机功耗 0.05-0.2kW
                telemetry.put("RealTimePower", 0.0);
                telemetry.put("DischargePower", 0.0);
                telemetry.put("ReactivePower", 0.0);
                telemetry.put("ChargeDuration", 0.0);
                telemetry.put("CumulativeEnergy", Math.round(Math.random() * 500));
                telemetry.put("SOC", 0.0);
                telemetry.put("temperature", Math.round(10 + Math.random() * 20)); // 10°C - 30°C
            }
        }
        
        // 添加时间戳
        telemetry.put("timestamp", System.currentTimeMillis());
        
        return telemetry;
    }
    
    /**
     * 获取 MQTT 连接状态
     */
    public boolean isMqttConnected() {
        return mqttClientService.isConnected();
    }
    
    /**
     * 获取已连接设备数量
     */
    public int getConnectedDeviceCount() {
        return mqttClientService.getAllDeviceStatus().size();
    }
}
