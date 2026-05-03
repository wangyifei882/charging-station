package com.charging.station.dto.iot;

import java.util.List;

public class IotDeviceListResponse {
    private Integer code;
    private String message;
    private DeviceListData data;
    
    public static class DeviceListData {
        private Integer total;
        private List<IotDevice> devices;
        
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        public List<IotDevice> getDevices() { return devices; }
        public void setDevices(List<IotDevice> devices) { this.devices = devices; }
    }
    
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public DeviceListData getData() { return data; }
    public void setData(DeviceListData data) { this.data = data; }
}
