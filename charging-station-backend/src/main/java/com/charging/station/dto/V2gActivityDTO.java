package com.charging.station.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class V2gActivityDTO {
    private Long id;
    private String activityNo;
    private String activityName;
    private String activityType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String regionCode;
    private String organizer;
    private BigDecimal targetDischargeEnergy;
    private BigDecimal targetPower;
    private BigDecimal actualDischargeEnergy;
    private Integer participatingStationCount;
    private Integer participatingVehicleCount;
    private Integer participantCount;
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
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
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
    public BigDecimal getTargetPower() { return targetPower != null ? targetPower : targetDischargeEnergy; }
    public void setTargetPower(BigDecimal targetPower) { this.targetPower = targetPower; }
    public BigDecimal getActualDischargeEnergy() { return actualDischargeEnergy; }
    public void setActualDischargeEnergy(BigDecimal actualDischargeEnergy) { this.actualDischargeEnergy = actualDischargeEnergy; }
    public Integer getParticipatingStationCount() { return participatingStationCount; }
    public void setParticipatingStationCount(Integer participatingStationCount) { this.participatingStationCount = participatingStationCount; }
    public Integer getParticipatingVehicleCount() { return participatingVehicleCount; }
    public void setParticipatingVehicleCount(Integer participatingVehicleCount) { this.participatingVehicleCount = participatingVehicleCount; }
    public Integer getParticipantCount() { return participantCount != null ? participantCount : participatingVehicleCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }
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
