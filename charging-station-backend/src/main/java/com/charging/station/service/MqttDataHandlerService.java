package com.charging.station.service;

import com.charging.station.entity.Device;
import com.charging.station.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class MqttDataHandlerService {
    
    @Autowired
    private DeviceMapper deviceMapper;
    
    /**
     * 处理实时数据
     */
    public void handleRealTimeData(String nodeId, Map<String, Object> data) {
        log.debug("处理设备 {} 的实时数据: {}", nodeId, data);
        
        try {
            // 根据 nodeId 查找设备
            Device device = findDeviceByNodeId(nodeId);
            if (device == null) {
                log.warn("未找到对应设备，nodeId: {}", nodeId);
                return;
            }
            
            // 更新设备实时数据字段
            updateDeviceTelemetry(device, data);
            
            // 更新设备状态为在线 (1=在线)
            device.setStatus(1);
            device.setLastCommTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            
            // 保存到数据库
            deviceMapper.updateById(device);
            log.debug("设备 {} 实时数据已更新", nodeId);
            
        } catch (Exception e) {
            log.error("处理实时数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理状态数据
     */
    public void handleStatusData(String nodeId, Map<String, Object> data) {
        log.debug("处理设备 {} 的状态数据: {}", nodeId, data);
        
        try {
            Device device = findDeviceByNodeId(nodeId);
            if (device == null) {
                log.warn("未找到对应设备，nodeId: {}", nodeId);
                return;
            }
            
            // 解析状态 (Integer 类型)
            Integer status = parseStatus(data);
            device.setStatus(status);
            device.setLastCommTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            
            deviceMapper.updateById(device);
            log.info("设备 {} 状态已更新为: {}", nodeId, status);
            
        } catch (Exception e) {
            log.error("处理状态数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理生命周期数据（设备上线/离线）
     */
    public void handleLifeCycleData(String nodeId, Map<String, Object> data) {
        log.info("处理设备 {} 的生命周期数据: {}", nodeId, data);
        
        try {
            Device device = findDeviceByNodeId(nodeId);
            if (device == null) {
                // 如果设备不存在，可能需要创建设备
                log.info("检测到新设备上线，nodeId: {}", nodeId);
                createNewDevice(nodeId, data);
                return;
            }
            
            String event = (String) data.get("event");
            if ("online".equals(event)) {
                device.setStatus(1); // 在线
                log.info("设备 {} 上线", nodeId);
            } else if ("offline".equals(event)) {
                device.setStatus(0); // 离线
                log.info("设备 {} 离线", nodeId);
            }
            
            device.setLastCommTime(LocalDateTime.now());
            device.setUpdateTime(LocalDateTime.now());
            deviceMapper.updateById(device);
            
        } catch (Exception e) {
            log.error("处理生命周期数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理命令回复
     */
    public void handleCmdReplyData(String nodeId, Map<String, Object> data) {
        log.info("处理设备 {} 的命令回复: {}", nodeId, data);
        // 可以在这里处理命令执行结果，比如更新订单状态等
    }
    
    /**
     * 根据 nodeId 查找设备
     */
    private Device findDeviceByNodeId(String nodeId) {
        // 假设 deviceCode 存储了 nodeId
        // 这里简化处理，实际应该用 deviceMapper.selectOne 查询
        return deviceMapper.selectById(nodeId);
    }
    
    /**
     * 更新设备遥测数据
     */
    private void updateDeviceTelemetry(Device device, Map<String, Object> data) {
        // 将 MQTT 数据映射到设备字段
        
        if (data.containsKey("ChargeVoltage")) {
            device.setRealTimeVoltage(getBigDecimalValue(data.get("ChargeVoltage")));
        }
        if (data.containsKey("ChargeCurrent")) {
            device.setRealTimeCurrent(getBigDecimalValue(data.get("ChargeCurrent")));
        }
        if (data.containsKey("ActivePower")) {
            device.setRealTimePower(getBigDecimalValue(data.get("ActivePower")));
        }
        if (data.containsKey("CumulativeEnergy")) {
            device.setTotalEnergy(getBigDecimalValue(data.get("CumulativeEnergy")));
        }
        if (data.containsKey("SOC")) {
            // SOC 字段在 Device 中没有，可以存储到 attributes 或其他方式
            // 这里暂时不处理
        }
        if (data.containsKey("temperature")) {
            // temperature 字段在 Device 中没有，可以存储到 attributes 或其他方式
            // 这里暂时不处理
        }
    }
    
    /**
     * 解析状态
     */
    private Integer parseStatus(Map<String, Object> data) {
        String status = (String) data.get("status");
        if (status == null) {
            Boolean online = (Boolean) data.get("online");
            if (online != null) {
                return online ? 1 : 0;
            } else {
                return 0;
            }
        }
        // 根据状态字符串返回对应的 Integer
        switch (status.toLowerCase()) {
            case "online":
            case "charging":
                return 1;
            case "idle":
                return 2;
            case "fault":
                return 3;
            case "offline":
            default:
                return 0;
        }
    }
    
    /**
     * 创建新设备
     */
    private void createNewDevice(String nodeId, Map<String, Object> data) {
        Device device = new Device();
        device.setDeviceCode(nodeId);
        device.setDeviceName("MQTT设备-" + nodeId);
        device.setStatus(1);
        device.setCreateTime(LocalDateTime.now());
        device.setUpdateTime(LocalDateTime.now());
        
        deviceMapper.insert(device);
        log.info("新设备已创建: {}", nodeId);
    }
    
    private BigDecimal getBigDecimalValue(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
