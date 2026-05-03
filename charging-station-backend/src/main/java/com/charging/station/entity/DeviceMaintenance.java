package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("device_maintenance")
public class DeviceMaintenance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deviceId;
    private Integer maintenanceType;
    private java.time.LocalDate maintenanceDate;
    private String maintenanceContent;
    private String maintenanceResult;
    private java.time.LocalDate nextMaintenanceDate;
    private Long operatorId;
    private java.math.BigDecimal cost;
    private String attachmentUrls;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Integer getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(Integer maintenanceType) { this.maintenanceType = maintenanceType; }
    public java.time.LocalDate getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(java.time.LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }
    public String getMaintenanceContent() { return maintenanceContent; }
    public void setMaintenanceContent(String maintenanceContent) { this.maintenanceContent = maintenanceContent; }
    public String getMaintenanceResult() { return maintenanceResult; }
    public void setMaintenanceResult(String maintenanceResult) { this.maintenanceResult = maintenanceResult; }
    public java.time.LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(java.time.LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public java.math.BigDecimal getCost() { return cost; }
    public void setCost(java.math.BigDecimal cost) { this.cost = cost; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
