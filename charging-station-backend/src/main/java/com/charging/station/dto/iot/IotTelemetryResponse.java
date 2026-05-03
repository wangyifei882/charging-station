package com.charging.station.dto.iot;

public class IotTelemetryResponse {
    private Integer code;
    private String message;
    private IotTelemetryData data;
    
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public IotTelemetryData getData() { return data; }
    public void setData(IotTelemetryData data) { this.data = data; }
}
