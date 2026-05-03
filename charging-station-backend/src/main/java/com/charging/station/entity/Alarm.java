package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("alarm")
public class Alarm {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String alarmNo;
    private Integer alarmType;
    private Integer alarmLevel;
    private Long deviceId;
    private Long stationId;
    private String alarmContent;
    private String alarmValue;
    private Integer status;
    private Long handlerId;
    private LocalDateTime handleTime;
    private String handleRemark;
    private LocalDateTime createTime;
    private String regionCode;
    private Long operatorId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAlarmNo() { return alarmNo; }
    public void setAlarmNo(String alarmNo) { this.alarmNo = alarmNo; }
    public Integer getAlarmType() { return alarmType; }
    public void setAlarmType(Integer alarmType) { this.alarmType = alarmType; }
    public Integer getAlarmLevel() { return alarmLevel; }
    public void setAlarmLevel(Integer alarmLevel) { this.alarmLevel = alarmLevel; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getAlarmContent() { return alarmContent; }
    public void setAlarmContent(String alarmContent) { this.alarmContent = alarmContent; }
    public String getAlarmValue() { return alarmValue; }
    public void setAlarmValue(String alarmValue) { this.alarmValue = alarmValue; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }
    public LocalDateTime getHandleTime() { return handleTime; }
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }
    public String getHandleRemark() { return handleRemark; }
    public void setHandleRemark(String handleRemark) { this.handleRemark = handleRemark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
}
