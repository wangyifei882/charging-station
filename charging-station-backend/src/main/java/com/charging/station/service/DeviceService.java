package com.charging.station.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Device;

import java.util.List;
import java.util.Map;

public interface DeviceService extends IService<Device> {
    Page<Device> getDevicePage(int page, int size, String deviceCode, Integer status, Long typeId);
    Device getDeviceById(Long id);
    void addDevice(Device device);
    void updateDevice(Long id, Device device);
    void deleteDevice(Long id);
    Map<String, Object> getDeviceStats();
    List<Device> getAllDevices();
    void updateDeviceStatus(Long id, Integer status);
}
