package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("access_application")
public class AccessApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String applicationNo;
    private Long operatorId;
    private Long stationId;
    private String stationName;
    private String stationAddress;
    private String deviceSummary;
    private Integer totalDevices;
    private Integer dcFastCount;
    private Integer acSlowCount;
    private Integer v2gCount;
    private String contactPerson;
    private String contactPhone;
    private LocalDateTime applicationTime;
    private Integer auditStatus;
    private String auditOpinion;
    private Long auditorId;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getStationAddress() { return stationAddress; }
    public void setStationAddress(String stationAddress) { this.stationAddress = stationAddress; }
    public String getDeviceSummary() { return deviceSummary; }
    public void setDeviceSummary(String deviceSummary) { this.deviceSummary = deviceSummary; }
    public Integer getTotalDevices() { return totalDevices; }
    public void setTotalDevices(Integer totalDevices) { this.totalDevices = totalDevices; }
    public Integer getDcFastCount() { return dcFastCount; }
    public void setDcFastCount(Integer dcFastCount) { this.dcFastCount = dcFastCount; }
    public Integer getAcSlowCount() { return acSlowCount; }
    public void setAcSlowCount(Integer acSlowCount) { this.acSlowCount = acSlowCount; }
    public Integer getV2gCount() { return v2gCount; }
    public void setV2gCount(Integer v2gCount) { this.v2gCount = v2gCount; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public LocalDateTime getApplicationTime() { return applicationTime; }
    public void setApplicationTime(LocalDateTime applicationTime) { this.applicationTime = applicationTime; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }
    public String getAuditOpinion() { return auditOpinion; }
    public void setAuditOpinion(String auditOpinion) { this.auditOpinion = auditOpinion; }
    public Long getAuditorId() { return auditorId; }
    public void setAuditorId(Long auditorId) { this.auditorId = auditorId; }
    public LocalDateTime getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDateTime auditTime) { this.auditTime = auditTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
