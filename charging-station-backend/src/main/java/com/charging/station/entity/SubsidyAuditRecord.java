package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("subsidy_audit_record")
public class SubsidyAuditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Integer auditStage;
    private Integer auditResult;
    private String auditOpinion;
    private Long auditorId;
    private String auditorName;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public Integer getAuditStage() { return auditStage; }
    public void setAuditStage(Integer auditStage) { this.auditStage = auditStage; }
    public Integer getAuditResult() { return auditResult; }
    public void setAuditResult(Integer auditResult) { this.auditResult = auditResult; }
    public String getAuditOpinion() { return auditOpinion; }
    public void setAuditOpinion(String auditOpinion) { this.auditOpinion = auditOpinion; }
    public Long getAuditorId() { return auditorId; }
    public void setAuditorId(Long auditorId) { this.auditorId = auditorId; }
    public String getAuditorName() { return auditorName; }
    public void setAuditorName(String auditorName) { this.auditorName = auditorName; }
    public LocalDateTime getAuditTime() { return auditTime; }
    public void setAuditTime(LocalDateTime auditTime) { this.auditTime = auditTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
