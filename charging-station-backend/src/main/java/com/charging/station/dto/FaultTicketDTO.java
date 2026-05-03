package com.charging.station.dto;

import java.time.LocalDateTime;

public class FaultTicketDTO {
    private Long id;
    private String ticketNo;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Long stationId;
    private Integer faultType;
    private String faultTypeName;
    private String faultDescription;
    private String faultImages;
    private Long reporterId;
    private Integer status;
    private String statusName;
    private Long handlerId;
    private String handlerCompany;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedFinishTime;
    private String solution;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public Integer getFaultType() { return faultType; }
    public void setFaultType(Integer faultType) { this.faultType = faultType; }
    public String getFaultTypeName() { return faultTypeName; }
    public void setFaultTypeName(String faultTypeName) { this.faultTypeName = faultTypeName; }
    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }
    public String getFaultImages() { return faultImages; }
    public void setFaultImages(String faultImages) { this.faultImages = faultImages; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }
    public String getHandlerCompany() { return handlerCompany; }
    public void setHandlerCompany(String handlerCompany) { this.handlerCompany = handlerCompany; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getExpectedFinishTime() { return expectedFinishTime; }
    public void setExpectedFinishTime(LocalDateTime expectedFinishTime) { this.expectedFinishTime = expectedFinishTime; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
