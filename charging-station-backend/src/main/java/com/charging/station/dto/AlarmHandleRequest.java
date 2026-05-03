package com.charging.station.dto;

public class AlarmHandleRequest {
    private Integer handlingType;
    private Integer newStatus;
    private String handlingContent;

    public Integer getHandlingType() { return handlingType; }
    public void setHandlingType(Integer handlingType) { this.handlingType = handlingType; }
    public Integer getNewStatus() { return newStatus; }
    public void setNewStatus(Integer newStatus) { this.newStatus = newStatus; }
    public String getHandlingContent() { return handlingContent; }
    public void setHandlingContent(String handlingContent) { this.handlingContent = handlingContent; }
}
