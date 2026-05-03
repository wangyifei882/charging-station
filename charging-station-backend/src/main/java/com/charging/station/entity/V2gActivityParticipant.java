package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("v2g_activity_participant")
public class V2gActivityParticipant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long operatorId;
    private Long stationId;
    private BigDecimal dischargeEnergy;
    private Integer dischargeDuration;
    private BigDecimal dischargeRevenue;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public BigDecimal getDischargeEnergy() { return dischargeEnergy; }
    public void setDischargeEnergy(BigDecimal dischargeEnergy) { this.dischargeEnergy = dischargeEnergy; }
    public Integer getDischargeDuration() { return dischargeDuration; }
    public void setDischargeDuration(Integer dischargeDuration) { this.dischargeDuration = dischargeDuration; }
    public BigDecimal getDischargeRevenue() { return dischargeRevenue; }
    public void setDischargeRevenue(BigDecimal dischargeRevenue) { this.dischargeRevenue = dischargeRevenue; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
