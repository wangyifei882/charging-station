package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("subsidy_application")
public class SubsidyApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String applicationNo;
    private Long operatorId;
    private Integer subsidyType;
    private String subsidyPeriod;
    private BigDecimal totalAmount;
    private String supportingData;
    private Integer currentAuditStage;
    private Integer auditStatus;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Integer getSubsidyType() { return subsidyType; }
    public void setSubsidyType(Integer subsidyType) { this.subsidyType = subsidyType; }
    public String getSubsidyPeriod() { return subsidyPeriod; }
    public void setSubsidyPeriod(String subsidyPeriod) { this.subsidyPeriod = subsidyPeriod; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getSupportingData() { return supportingData; }
    public void setSupportingData(String supportingData) { this.supportingData = supportingData; }
    public Integer getCurrentAuditStage() { return currentAuditStage; }
    public void setCurrentAuditStage(Integer currentAuditStage) { this.currentAuditStage = currentAuditStage; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
