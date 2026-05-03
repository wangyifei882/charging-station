package com.charging.station.vo;

public class StationVO {
    private Long id;
    private String stationName;
    private String operatorName;
    private String regionCode;
    private String regionName;
    private String address;
    private Integer totalDevices;
    private Integer onlineDevices;
    private String todayCharge;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getTotalDevices() { return totalDevices; }
    public void setTotalDevices(Integer totalDevices) { this.totalDevices = totalDevices; }
    public Integer getOnlineDevices() { return onlineDevices; }
    public void setOnlineDevices(Integer onlineDevices) { this.onlineDevices = onlineDevices; }
    public String getTodayCharge() { return todayCharge; }
    public void setTodayCharge(String todayCharge) { this.todayCharge = todayCharge; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
