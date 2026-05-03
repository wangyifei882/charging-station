package com.charging.station.dto;

public class EmergencyCommandRequest {
    private Integer commandType;
    private Long targetOperatorId;
    private Long targetStationId;
    private String targetStationIds;
    private String commandContent;
    private String issueReason;
    private String startTime;
    private String endTime;
    private Integer maxPowerLimit;
    private Integer validDuration;

    public Integer getCommandType() { return commandType; }
    public void setCommandType(Integer commandType) { this.commandType = commandType; }
    public Long getTargetOperatorId() { return targetOperatorId; }
    public void setTargetOperatorId(Long targetOperatorId) { this.targetOperatorId = targetOperatorId; }
    public Long getTargetStationId() { return targetStationId; }
    public void setTargetStationId(Long targetStationId) { this.targetStationId = targetStationId; }
    public String getTargetStationIds() { return targetStationIds; }
    public void setTargetStationIds(String targetStationIds) { this.targetStationIds = targetStationIds; }
    public String getCommandContent() { 
        if (commandContent != null && !commandContent.trim().isEmpty()) {
            return commandContent;
        }
        return issueReason;
    }
    public void setCommandContent(String commandContent) { this.commandContent = commandContent; }
    public String getIssueReason() { return issueReason; }
    public void setIssueReason(String issueReason) { this.issueReason = issueReason; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getMaxPowerLimit() { return maxPowerLimit; }
    public void setMaxPowerLimit(Integer maxPowerLimit) { this.maxPowerLimit = maxPowerLimit; }
    public Integer getValidDuration() { return validDuration; }
    public void setValidDuration(Integer validDuration) { this.validDuration = validDuration; }
}
