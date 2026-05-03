package com.charging.station.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubsidyApplicationDTO {
    private Long id;
    private String applicationNo;
    private Long operatorId;
    private String operatorName;
    private Integer subsidyType;
    private String subsidyTypeName;
    private BigDecimal totalAmount;
    private BigDecimal applyAmount;
    private Integer currentAuditStage;
    private Integer auditStatus;
    private String auditStatusName;
    private Integer status;
    private LocalDateTime createTime;
    private String applyTime;
    private LocalDateTime updateTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public Integer getSubsidyType() { return subsidyType; }
    public void setSubsidyType(Integer subsidyType) { 
        this.subsidyType = subsidyType;
        this.subsidyTypeName = (subsidyType != null && subsidyType == 1) ? "建设补贴" : "运营补贴";
    }
    public String getSubsidyTypeName() { return subsidyTypeName; }
    public void setSubsidyTypeName(String subsidyTypeName) { this.subsidyTypeName = subsidyTypeName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount;
        this.applyAmount = totalAmount;
    }
    public BigDecimal getApplyAmount() { return applyAmount != null ? applyAmount : totalAmount; }
    public void setApplyAmount(BigDecimal applyAmount) { this.applyAmount = applyAmount; }
    public Integer getCurrentAuditStage() { return currentAuditStage; }
    public void setCurrentAuditStage(Integer currentAuditStage) { this.currentAuditStage = currentAuditStage; }
    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { 
        this.auditStatus = auditStatus;
        if (auditStatus != null) {
            switch (auditStatus) {
                case 0: this.auditStatusName = "待审核"; break;
                case 1: this.auditStatusName = "已通过"; break;
                case 2: this.auditStatusName = "已驳回"; break;
                default: this.auditStatusName = "未知"; break;
            }
        }
    }
    public String getAuditStatusName() { return auditStatusName; }
    public void setAuditStatusName(String auditStatusName) { this.auditStatusName = auditStatusName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { 
        this.createTime = createTime;
        this.applyTime = createTime != null ? createTime.format(FORMATTER) : null;
    }
    public String getApplyTime() { return applyTime != null ? applyTime : (createTime != null ? createTime.format(FORMATTER) : null); }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
