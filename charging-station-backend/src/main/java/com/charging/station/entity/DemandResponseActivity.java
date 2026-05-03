package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("demand_response_activity")
public class DemandResponseActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String activityNo;
    private String activityName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer responseType;
    private BigDecimal targetEnergy;
    private BigDecimal actualEnergy;
    private BigDecimal completionRate;
    private Integer participatingOperatorCount;
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
    public void setResponseType(Integer responseType) { this.responseType = responseType; }
    public BigDecimal getTargetEnergy() { return targetEnergy; }
    public void setTargetEnergy(BigDecimal targetEnergy) { this.targetEnergy = targetEnergy; }
    public BigDecimal getActualEnergy() { return actualEnergy; }
    public void setActualEnergy(BigDecimal actualEnergy) { this.actualEnergy = actualEnergy; }
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    public Integer getParticipatingOperatorCount() { return participatingOperatorCount; }
    public void setParticipatingOperatorCount(Integer participatingOperatorCount) { this.participatingOperatorCount = participatingOperatorCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
