package com.charging.station.dto.iot;

import java.util.List;

public class IotBatchTelemetryResponse {
    private Integer code;
    private String message;
    private List<IotTelemetryData> data;
    
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<IotTelemetryData> getData() { return data; }
    public void setData(List<IotTelemetryData> data) { this.data = data; }
}
