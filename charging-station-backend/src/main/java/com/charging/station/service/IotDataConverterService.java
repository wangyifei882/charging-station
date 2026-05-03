package com.charging.station.service;

import com.charging.station.dto.iot.IotDevice;
import com.charging.station.dto.iot.IotTelemetryData;
import com.charging.station.entity.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class IotDataConverterService {
    
    private static final Logger logger = LoggerFactory.getLogger(IotDataConverterService.class);
    
    public Device convertToLocalDevice(IotDevice iotDevice) {
        Device device = new Device();
        device.setDeviceCode(iotDevice.getIotDeviceId());
        device.setDeviceName(iotDevice.getDeviceName());
        
        if (iotDevice.getLocation() != null) {
            device.setAreaCode(iotDevice.getLocation().getArea());
            device.setPosition(iotDevice.getLocation().getPosition());
        }
        
        if (iotDevice.getAttributes() != null) {
            Map<String, Object> attrs = iotDevice.getAttributes();
            device.setManufacturer((String) attrs.get("manufacturer"));
            device.setModel((String) attrs.get("model"));
            if (attrs.get("powerRating") != null) {
                device.setPowerRating(new BigDecimal(attrs.get("powerRating").toString()));
            }
            device.setFirmwareVersion((String) attrs.get("firmwareVersion"));
            
            // 从 attributes 中直接获取实时功率（用于模拟数据）
            if (attrs.get("realTimePower") != null) {
                device.setRealTimePower(new BigDecimal(attrs.get("realTimePower").toString()));
            }
        }
        
        device.setStatus(mapDeviceStatus(iotDevice.getStatus()));
        return device;
    }
    
    public void updateDeviceFromTelemetry(Device device, IotTelemetryData telemetry) {
        if (telemetry.getTelemetry() != null) {
            Map<String, Object> data = telemetry.getTelemetry();
            
            if (data.get("status") != null) {
                device.setStatus(mapDeviceStatus((String) data.get("status")));
            }
            
            // 支持多种功率字段名
            Object powerValue = null;
            if (data.get("ActivePower") != null) {
                powerValue = data.get("ActivePower");
            } else if (data.get("RealTimePower") != null) {
                powerValue = data.get("RealTimePower");
            } else if (data.get("power") != null) {
                powerValue = data.get("power");
            }
            if (powerValue != null) {
                device.setRealTimePower(new BigDecimal(powerValue.toString()));
            }
            
            // 支持多种电压字段名
            Object voltageValue = null;
            if (data.get("ChargeVoltage") != null) {
                voltageValue = data.get("ChargeVoltage");
            } else if (data.get("voltage") != null) {
                voltageValue = data.get("voltage");
            }
            if (voltageValue != null) {
                device.setRealTimeVoltage(new BigDecimal(voltageValue.toString()));
            }
            
            // 支持多种电流字段名
            Object currentValue = null;
            if (data.get("ChargeCurrent") != null) {
                currentValue = data.get("ChargeCurrent");
            } else if (data.get("current") != null) {
                currentValue = data.get("current");
            }
            if (currentValue != null) {
                device.setRealTimeCurrent(new BigDecimal(currentValue.toString()));
            }
            
            // 支持多种能量字段名
            Object energy = null;
            if (data.get("CumulativeEnergy") != null) {
                energy = data.get("CumulativeEnergy");
            } else if (data.get("totalEnergy") != null) {
                energy = data.get("totalEnergy");
            } else if (data.get("energy") != null) {
                energy = data.get("energy");
            }
            if (energy != null) {
                device.setTotalEnergy(new BigDecimal(energy.toString()));
            }
        }
    }
    
    private Integer mapDeviceStatus(String iotStatus) {
        if (iotStatus == null) return 0;
        
        switch (iotStatus.toLowerCase()) {
            case "online":
            case "idle":
            case "charging":
            case "discharging":
                return 1; // 在线
            case "fault":
                return 2; // 故障
            case "maintenance":
                return 3; // 维护
            case "offline":
            default:
                return 0; // 离线
        }
    }
}
