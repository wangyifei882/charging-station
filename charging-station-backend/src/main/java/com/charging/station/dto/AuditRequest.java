package com.charging.station.dto;

public class AuditRequest {
    private Integer auditResult;
    private String auditOpinion;

    public Integer getAuditResult() { return auditResult; }
    public void setAuditResult(Integer auditResult) { this.auditResult = auditResult; }
    public String getAuditOpinion() { return auditOpinion; }
    public void setAuditOpinion(String auditOpinion) { this.auditOpinion = auditOpinion; }
}
