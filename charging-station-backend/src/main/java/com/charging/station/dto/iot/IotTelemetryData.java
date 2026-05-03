package com.charging.station.dto.iot;

import java.util.Map;

public class IotTelemetryData {
    private String iotDeviceId;
    private String timestamp;
    private Map<String, Object> telemetry;
    private String quality;
    
    public String getIotDeviceId() { return iotDeviceId; }
    public void setIotDeviceId(String iotDeviceId) { this.iotDeviceId = iotDeviceId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Map<String, Object> getTelemetry() { return telemetry; }
    public void setTelemetry(Map<String, Object> telemetry) { this.telemetry = telemetry; }
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
}
