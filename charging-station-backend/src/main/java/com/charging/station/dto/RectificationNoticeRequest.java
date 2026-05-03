package com.charging.station.dto;

public class RectificationNoticeRequest {
    private Integer noticeType;
    private String issueReason;
    private String rectificationRequirement;
    private String deadline;

    public Integer getNoticeType() { return noticeType; }
    public void setNoticeType(Integer noticeType) { this.noticeType = noticeType; }
    public String getIssueReason() { return issueReason; }
    public void setIssueReason(String issueReason) { this.issueReason = issueReason; }
    public String getRectificationRequirement() { return rectificationRequirement; }
    public void setRectificationRequirement(String rectificationRequirement) { this.rectificationRequirement = rectificationRequirement; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
