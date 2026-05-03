package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("v2g_activity")
public class V2gActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String activityNo;
    private String activityName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String regionCode;
    private String organizer;
    private BigDecimal targetDischargeEnergy;
    private BigDecimal actualDischargeEnergy;
    private Integer participatingStationCount;
    private Integer participatingVehicleCount;
    private BigDecimal avgDischargePrice;
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
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public BigDecimal getTargetDischargeEnergy() { return targetDischargeEnergy; }
    public void setTargetDischargeEnergy(BigDecimal targetDischargeEnergy) { this.targetDischargeEnergy = targetDischargeEnergy; }
    public BigDecimal getActualDischargeEnergy() { return actualDischargeEnergy; }
    public void setActualDischargeEnergy(BigDecimal actualDischargeEnergy) { this.actualDischargeEnergy = actualDischargeEnergy; }
    public Integer getParticipatingStationCount() { return participatingStationCount; }
    public void setParticipatingStationCount(Integer participatingStationCount) { this.participatingStationCount = participatingStationCount; }
    public Integer getParticipatingVehicleCount() { return participatingVehicleCount; }
    public void setParticipatingVehicleCount(Integer participatingVehicleCount) { this.participatingVehicleCount = participatingVehicleCount; }
    public BigDecimal getAvgDischargePrice() { return avgDischargePrice; }
    public void setAvgDischargePrice(BigDecimal avgDischargePrice) { this.avgDischargePrice = avgDischargePrice; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
