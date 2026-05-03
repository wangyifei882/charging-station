package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("dispatch_history")
public class DispatchHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long stationId;
    private BigDecimal chargePowerLimit;
    private BigDecimal dischargePowerLimit;
    private LocalDateTime effectiveStartTime;
    private LocalDateTime effectiveEndTime;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public BigDecimal getChargePowerLimit() { return chargePowerLimit; }
    public void setChargePowerLimit(BigDecimal chargePowerLimit) { this.chargePowerLimit = chargePowerLimit; }
    public BigDecimal getDischargePowerLimit() { return dischargePowerLimit; }
    public void setDischargePowerLimit(BigDecimal dischargePowerLimit) { this.dischargePowerLimit = dischargePowerLimit; }
    public LocalDateTime getEffectiveStartTime() { return effectiveStartTime; }
    public void setEffectiveStartTime(LocalDateTime effectiveStartTime) { this.effectiveStartTime = effectiveStartTime; }
    public LocalDateTime getEffectiveEndTime() { return effectiveEndTime; }
    public void setEffectiveEndTime(LocalDateTime effectiveEndTime) { this.effectiveEndTime = effectiveEndTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
