package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("demand_response_operator_record")
public class DemandResponseOperatorRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long operatorId;
    private BigDecimal targetEnergy;
    private BigDecimal actualEnergy;
    private BigDecimal completionRate;
    private Integer isQualified;
    @TableField("`rank`")
    private Integer rank;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public BigDecimal getTargetEnergy() { return targetEnergy; }
    public void setTargetEnergy(BigDecimal targetEnergy) { this.targetEnergy = targetEnergy; }
    public BigDecimal getActualEnergy() { return actualEnergy; }
    public void setActualEnergy(BigDecimal actualEnergy) { this.actualEnergy = actualEnergy; }
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    public Integer getIsQualified() { return isQualified; }
    public void setIsQualified(Integer isQualified) { this.isQualified = isQualified; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
