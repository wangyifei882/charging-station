package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.entity.Station;
import com.charging.station.entity.Device;
import com.charging.station.service.FacilityService;
import com.charging.station.service.StationService;
import com.charging.station.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    private StationService stationService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public IPage<Station> getStationLedger(int page, int size, String regionCode, Long operatorId, Integer accessStatus, String keyword) {
        Page<Station> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        if (regionCode != null) {
            wrapper.eq(Station::getRegionCode, regionCode);
        }
        if (operatorId != null) {
            wrapper.eq(Station::getOperatorId, operatorId);
        }
        if (accessStatus != null) {
            wrapper.eq(Station::getAccessStatus, accessStatus);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Station::getName, keyword).or().like(Station::getAddress, keyword);
        }
        wrapper.orderByDesc(Station::getCreateTime);
        return stationService.page(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getStationDetail(Long stationId) {
        Map<String, Object> result = new HashMap<>();
        Station station = stationService.getById(stationId);
        result.put("stationInfo", station);

        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(Device::getStationId, stationId);
        result.put("deviceList", deviceService.list(deviceWrapper));

        return result;
    }

    @Override
    public IPage<Device> getStatusMonitor(int page, int size, String regionCode, Long operatorId, Integer status, Integer faultType) {
        Page<Device> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (regionCode != null) {
            wrapper.eq(Device::getRegionCode, regionCode);
        }
        if (operatorId != null) {
            wrapper.eq(Device::getOperatorId, operatorId);
        }
        if (status != null) {
            wrapper.eq(Device::getStatus, status);
        }
        wrapper.orderByDesc(Device::getLastCommTime);
        return deviceService.page(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getDeviceDetail(String deviceCode) {
        Map<String, Object> result = new HashMap<>();
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceCode, deviceCode);
        Device device = deviceService.getOne(wrapper);
        result.put("deviceArchive", device);
        return result;
    }
}
