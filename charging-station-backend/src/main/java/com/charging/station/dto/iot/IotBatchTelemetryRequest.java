package com.charging.station.dto.iot;

import java.util.List;

public class IotBatchTelemetryRequest {
    private List<String> deviceIds;
    private List<String> metrics;
    
    public List<String> getDeviceIds() { return deviceIds; }
    public void setDeviceIds(List<String> deviceIds) { this.deviceIds = deviceIds; }
    public List<String> getMetrics() { return metrics; }
    public void setMetrics(List<String> metrics) { this.metrics = metrics; }
}
