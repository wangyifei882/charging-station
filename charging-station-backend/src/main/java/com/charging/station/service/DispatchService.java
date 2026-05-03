package com.charging.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.DispatchTask;
import java.math.BigDecimal;
import java.util.Map;

public interface DispatchService extends IService<DispatchTask> {
    Map<String, Object> getOverview(String taskNo);
    Map<String, Object> getRegionPowerAllocation(String taskNo);
    Map<String, Object> getPowerTrend(String taskNo, Integer hours);
    Map<String, Object> getStationPowerDetail(String taskNo, Long stationId);
    void adjustStationPower(String taskNo, Long stationId, BigDecimal newChargePower, BigDecimal newDischargePower);
    void issueDispatchCommand(String taskNo);
}
