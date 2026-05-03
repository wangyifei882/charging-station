package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("emergency_dispatch_command")
public class EmergencyDispatchCommand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String commandNo;
    private Integer commandType;
    private Long targetOperatorId;
    private Long targetStationId;
    private String commandContent;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long issuerId;
    private LocalDateTime issueTime;
    private Integer status;
    private String executionResult;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCommandNo() { return commandNo; }
    public void setCommandNo(String commandNo) { this.commandNo = commandNo; }
    public Integer getCommandType() { return commandType; }
    public void setCommandType(Integer commandType) { this.commandType = commandType; }
    public Long getTargetOperatorId() { return targetOperatorId; }
    public void setTargetOperatorId(Long targetOperatorId) { this.targetOperatorId = targetOperatorId; }
    public Long getTargetStationId() { return targetStationId; }
    public void setTargetStationId(Long targetStationId) { this.targetStationId = targetStationId; }
    public String getCommandContent() { return commandContent; }
    public void setCommandContent(String commandContent) { this.commandContent = commandContent; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Long getIssuerId() { return issuerId; }
    public void setIssuerId(Long issuerId) { this.issuerId = issuerId; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { this.issueTime = issueTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getExecutionResult() { return executionResult; }
    public void setExecutionResult(String executionResult) { this.executionResult = executionResult; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
