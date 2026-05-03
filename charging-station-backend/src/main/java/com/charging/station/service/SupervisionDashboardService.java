package com.charging.station.service;

import java.util.Map;

public interface SupervisionDashboardService {
    Map<String, Object> getDashboardMetrics(String regionCode);
    Map<String, Object> getChargeDischargeTrend(Integer days, String regionCode, String aggregate);
    Map<String, Object> getDemandResponseEffect(Integer days, String regionCode);
}
