package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.charging.station.entity.Station;
import com.charging.station.entity.Device;
import java.util.Map;
import java.util.List;

public interface FacilityService {
    IPage<Station> getStationLedger(int page, int size, String regionCode, Long operatorId, Integer accessStatus, String keyword);
    Map<String, Object> getStationDetail(Long stationId);
    IPage<Device> getStatusMonitor(int page, int size, String regionCode, Long operatorId, Integer status, Integer faultType);
    Map<String, Object> getDeviceDetail(String deviceCode);
}
