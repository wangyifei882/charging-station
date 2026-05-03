package com.charging.station.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DemandResponseActivityDTO {
    private Long id;
    private String activityNo;
    private String activityName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer responseType;
    private String responseTypeName;
    private BigDecimal targetEnergy;
    private BigDecimal targetPower;
    private BigDecimal actualEnergy;
    private BigDecimal actualPower;
    private BigDecimal completionRate;
    private BigDecimal responseRate;
    private Integer participatingOperatorCount;
    private Integer participantCount;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getActivityNo() { return activityNo; }
    public void setActivityNo(String activityNo) { this.activityNo = activityNo; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getResponseType() { return responseType; }
    public void setResponseType(Integer responseType) { 
        this.responseType = responseType;
        this.responseTypeName = (responseType != null && responseType == 1) ? "peak" : "fill";
    }
    public String getResponseTypeName() { return responseTypeName; }
    public void setResponseTypeName(String responseTypeName) { this.responseTypeName = responseTypeName; }
    public BigDecimal getTargetEnergy() { return targetEnergy; }
    public void setTargetEnergy(BigDecimal targetEnergy) { 
        this.targetEnergy = targetEnergy;
        this.targetPower = targetEnergy;
    }
    public BigDecimal getTargetPower() { return targetPower != null ? targetPower : targetEnergy; }
    public void setTargetPower(BigDecimal targetPower) { this.targetPower = targetPower; }
    public BigDecimal getActualEnergy() { return actualEnergy; }
    public void setActualEnergy(BigDecimal actualEnergy) { 
        this.actualEnergy = actualEnergy;
        this.actualPower = actualEnergy;
    }
    public BigDecimal getActualPower() { return actualPower != null ? actualPower : actualEnergy; }
    public void setActualPower(BigDecimal actualPower) { this.actualPower = actualPower; }
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { 
        this.completionRate = completionRate;
        this.responseRate = completionRate;
    }
    public BigDecimal getResponseRate() { return responseRate != null ? responseRate : completionRate; }
    public void setResponseRate(BigDecimal responseRate) { this.responseRate = responseRate; }
    public Integer getParticipatingOperatorCount() { return participatingOperatorCount; }
    public void setParticipatingOperatorCount(Integer participatingOperatorCount) { 
        this.participatingOperatorCount = participatingOperatorCount;
        this.participantCount = participatingOperatorCount;
    }
    public Integer getParticipantCount() { return participantCount != null ? participantCount : participatingOperatorCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
