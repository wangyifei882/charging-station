package com.charging.station.dto;

public class SubsidyAuditRequest {
    private Integer auditStage;
    private Integer auditResult;
    private String auditOpinion;

    public Integer getAuditStage() { return auditStage; }
    public void setAuditStage(Integer auditStage) { this.auditStage = auditStage; }
    public Integer getAuditResult() { return auditResult; }
    public void setAuditResult(Integer auditResult) { this.auditResult = auditResult; }
    public String getAuditOpinion() { return auditOpinion; }
    public void setAuditOpinion(String auditOpinion) { this.auditOpinion = auditOpinion; }
}
