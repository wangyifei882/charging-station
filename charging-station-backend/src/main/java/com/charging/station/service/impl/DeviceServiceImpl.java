package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.iot.IotDevice;
import com.charging.station.dto.iot.IotDeviceListResponse;
import com.charging.station.dto.iot.IotTelemetryData;
import com.charging.station.dto.iot.IotBatchTelemetryResponse;
import com.charging.station.entity.Device;
import com.charging.station.mapper.DeviceMapper;
import com.charging.station.service.DeviceService;
import com.charging.station.service.IotPlatformClientService;
import com.charging.station.service.IotDataConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);
    
    @Autowired(required = false)
    private IotPlatformClientService iotClientService;
    
    @Autowired
    private IotDataConverterService iotConverterService;
    
    @Override
    public Page<Device> getDevicePage(int page, int size, String deviceCode, Integer status, Long typeId) {
        try {
            if (iotClientService != null) {
                logger.info("正在从物联网平台获取设备列表...");
                IotDeviceListResponse iotResponse = iotClientService.getDeviceList(page, size, null, null);
                if (iotResponse != null && iotResponse.getCode() == 200 && iotResponse.getData() != null) {
                    return convertToPage(iotResponse, page, size, deviceCode, status);
                }
            }
        } catch (Exception e) {
            logger.warn("从物联网平台获取设备失败，使用本地数据", e);
        }
        
        return getDevicePageFromDB(page, size, deviceCode, status, typeId);
    }
    
    private Page<Device> getDevicePageFromDB(int page, int size, String deviceCode, Integer status, Long typeId) {
        Page<Device> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (deviceCode != null && !deviceCode.isEmpty()) {
            wrapper.like(Device::getDeviceCode, deviceCode);
        }
        if (status != null) {
            wrapper.eq(Device::getStatus, status);
        }
        if (typeId != null) {
            wrapper.eq(Device::getTypeId, typeId);
        }
        wrapper.orderByDesc(Device::getCreateTime);
        return page(pageParam, wrapper);
    }
    
    private Page<Device> convertToPage(IotDeviceListResponse iotResponse, int page, int size, String deviceCode, Integer status) {
        List<IotDevice> iotDevices = iotResponse.getData().getDevices();
        
        List<Device> devices = iotDevices.stream()
                .map(iotConverterService::convertToLocalDevice)
                .filter(device -> {
                    if (deviceCode != null && !deviceCode.isEmpty()) {
                        return device.getDeviceCode() != null && device.getDeviceCode().contains(deviceCode);
                    }
                    return true;
                })
                .filter(device -> {
                    if (status != null) {
                        return Objects.equals(device.getStatus(), status);
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        enrichWithTelemetryData(devices, iotDevices);
        
        Page<Device> resultPage = new Page<>(page, size);
        resultPage.setRecords(devices);
        resultPage.setTotal(iotResponse.getData().getTotal() != null ? iotResponse.getData().getTotal() : devices.size());
        
        return resultPage;
    }
    
    private void enrichWithTelemetryData(List<Device> devices, List<IotDevice> iotDevices) {
        if (iotClientService == null || iotDevices.isEmpty()) return;
        
        try {
            List<String> deviceIds = iotDevices.stream()
                    .map(IotDevice::getIotDeviceId)
                    .collect(Collectors.toList());
            
            IotBatchTelemetryResponse batchResponse = iotClientService.batchGetDeviceTelemetry(deviceIds);
            if (batchResponse != null && batchResponse.getCode() == 200 && batchResponse.getData() != null) {
                Map<String, IotTelemetryData> telemetryMap = batchResponse.getData().stream()
                        .collect(Collectors.toMap(IotTelemetryData::getIotDeviceId, t -> t));
                
                for (Device device : devices) {
                    IotTelemetryData telemetry = telemetryMap.get(device.getDeviceCode());
                    if (telemetry != null) {
                        iotConverterService.updateDeviceFromTelemetry(device, telemetry);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("批量获取设备遥测数据失败", e);
        }
    }

    @Override
    public Device getDeviceById(Long id) {
        return getById(id);
    }

    @Override
    public void addDevice(Device device) {
        save(device);
    }

    @Override
    public void updateDevice(Long id, Device device) {
        device.setId(id);
        updateById(device);
    }

    @Override
    public void deleteDevice(Long id) {
        removeById(id);
    }

    @Override
    public Map<String, Object> getDeviceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            if (iotClientService != null) {
                IotDeviceListResponse response = iotClientService.getDeviceList(1, 1000, null, null);
                if (response != null && response.getCode() == 200 && response.getData() != null) {
                    List<IotDevice> devices = response.getData().getDevices();
                    stats.put("totalCount", response.getData().getTotal() != null ? response.getData().getTotal() : devices.size());
                    
                    long onlineCount = devices.stream()
                            .filter(d -> "online".equals(d.getStatus()) || "idle".equals(d.getStatus()) || "charging".equals(d.getStatus()))
                            .count();
                    long offlineCount = devices.stream().filter(d -> "offline".equals(d.getStatus())).count();
                    long faultCount = devices.stream().filter(d -> "fault".equals(d.getStatus())).count();
                    
                    stats.put("onlineCount", onlineCount);
                    stats.put("offlineCount", offlineCount);
                    stats.put("faultCount", faultCount);
                    return stats;
                }
            }
        } catch (Exception e) {
            logger.warn("从物联网平台获取统计失败", e);
        }
        
        stats.put("totalCount", count());
        return stats;
    }

    @Override
    public List<Device> getAllDevices() {
        try {
            if (iotClientService != null) {
                IotDeviceListResponse response = iotClientService.getDeviceList(1, 1000, null, null);
                if (response != null && response.getCode() == 200 && response.getData() != null) {
                    List<Device> devices = response.getData().getDevices().stream()
                            .map(iotConverterService::convertToLocalDevice)
                            .collect(Collectors.toList());
                    enrichWithTelemetryData(devices, response.getData().getDevices());
                    return devices;
                }
            }
        } catch (Exception e) {
            logger.warn("从物联网平台获取设备失败", e);
        }
        
        return list();
    }

    @Override
    public void updateDeviceStatus(Long id, Integer status) {
        Device device = new Device();
        device.setId(id);
        device.setStatus(status);
        updateById(device);
    }
}
