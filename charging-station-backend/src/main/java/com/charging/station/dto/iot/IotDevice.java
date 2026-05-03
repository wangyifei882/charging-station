package com.charging.station.dto.iot;

import java.util.Map;

public class IotDevice {
    private String iotDeviceId;
    private String deviceName;
    private String deviceType;
    private String subType;
    private String status;
    private Location location;
    private Map<String, Object> attributes;
    private String onlineTime;
    private String lastReportTime;
    
    public static class Location {
        private String area;
        private String position;
        
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }
    
    public String getIotDeviceId() { return iotDeviceId; }
    public void setIotDeviceId(String iotDeviceId) { this.iotDeviceId = iotDeviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public String getOnlineTime() { return onlineTime; }
    public void setOnlineTime(String onlineTime) { this.onlineTime = onlineTime; }
    public String getLastReportTime() { return lastReportTime; }
    public void setLastReportTime(String lastReportTime) { this.lastReportTime = lastReportTime; }
}
