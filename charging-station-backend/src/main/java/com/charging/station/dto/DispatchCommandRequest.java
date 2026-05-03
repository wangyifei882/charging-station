package com.charging.station.dto;

import java.util.List;

public class DispatchCommandRequest {
    private List<Long> targetStationIds;
    private String commandType;
    private String commandContent;

    public List<Long> getTargetStationIds() { return targetStationIds; }
    public void setTargetStationIds(List<Long> targetStationIds) { this.targetStationIds = targetStationIds; }
    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }
    public String getCommandContent() { return commandContent; }
    public void setCommandContent(String commandContent) { this.commandContent = commandContent; }
}
