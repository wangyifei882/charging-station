package com.charging.station.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmergencyCommandDTO {
    private Long id;
    private String commandNo;
    private Integer commandType;
    private String commandTypeName;
    private Long targetOperatorId;
    private String targetOperatorName;
    private Long targetStationId;
    private String targetStationName;
    private String commandContent;
    private String issueReason;
    private LocalDateTime startTime;
    private String startTimeStr;
    private LocalDateTime endTime;
    private String endTimeStr;
    private Long issuerId;
    private LocalDateTime issueTime;
    private String issueTimeStr;
    private Integer status;
    private String statusName;
    private BigDecimal executionRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCommandNo() { return commandNo; }
    public void setCommandNo(String commandNo) { this.commandNo = commandNo; }
    public Integer getCommandType() { return commandType; }
    public void setCommandType(Integer commandType) { 
        this.commandType = commandType;
        if (commandType != null) {
            switch (commandType) {
                case 1: this.commandTypeName = "紧急停机"; break;
                case 2: this.commandTypeName = "功率限制"; break;
                case 3: this.commandTypeName = "限流"; break;
                default: this.commandTypeName = "其他"; break;
            }
        }
    }
    public String getCommandTypeName() { return commandTypeName; }
    public void setCommandTypeName(String commandTypeName) { this.commandTypeName = commandTypeName; }
    public Long getTargetOperatorId() { return targetOperatorId; }
    public void setTargetOperatorId(Long targetOperatorId) { this.targetOperatorId = targetOperatorId; }
    public String getTargetOperatorName() { return targetOperatorName; }
    public void setTargetOperatorName(String targetOperatorName) { this.targetOperatorName = targetOperatorName; }
    public Long getTargetStationId() { return targetStationId; }
    public void setTargetStationId(Long targetStationId) { this.targetStationId = targetStationId; }
    public String getTargetStationName() { return targetStationName; }
    public void setTargetStationName(String targetStationName) { this.targetStationName = targetStationName; }
    public String getCommandContent() { return commandContent; }
    public void setCommandContent(String commandContent) { 
        this.commandContent = commandContent;
        this.issueReason = commandContent;
    }
    public String getIssueReason() { return issueReason != null ? issueReason : commandContent; }
    public void setIssueReason(String issueReason) { this.issueReason = issueReason; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { 
        this.startTime = startTime;
        this.startTimeStr = startTime != null ? startTime.format(FORMATTER) : null;
    }
    public String getStartTimeStr() { return startTimeStr; }
    public void setStartTimeStr(String startTimeStr) { this.startTimeStr = startTimeStr; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { 
        this.endTime = endTime;
        this.endTimeStr = endTime != null ? endTime.format(FORMATTER) : null;
    }
    public String getEndTimeStr() { return endTimeStr; }
    public void setEndTimeStr(String endTimeStr) { this.endTimeStr = endTimeStr; }
    public Long getIssuerId() { return issuerId; }
    public void setIssuerId(Long issuerId) { this.issuerId = issuerId; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { 
        this.issueTime = issueTime;
        this.issueTimeStr = issueTime != null ? issueTime.format(FORMATTER) : null;
    }
    public String getIssueTimeStr() { return issueTimeStr; }
    public void setIssueTimeStr(String issueTimeStr) { this.issueTimeStr = issueTimeStr; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { 
        this.status = status;
        if (status != null) {
            switch (status) {
                case 1: this.statusName = "执行中"; break;
                case 2: this.statusName = "已完成"; break;
                case 3: this.statusName = "已撤销"; break;
                default: this.statusName = "未知"; break;
            }
        }
    }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public BigDecimal getExecutionRate() { return executionRate; }
    public void setExecutionRate(BigDecimal executionRate) { this.executionRate = executionRate; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
