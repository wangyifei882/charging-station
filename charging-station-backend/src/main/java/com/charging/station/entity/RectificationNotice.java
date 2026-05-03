package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("rectification_notice")
public class RectificationNotice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String noticeNo;
    private Integer noticeType;
    private Long targetOperatorId;
    private Long targetDeviceId;
    private Long targetStationId;
    private String issueReason;
    private String rectificationRequirement;
    private LocalDate deadline;
    private Long issuerId;
    private LocalDateTime issueTime;
    private Integer status;
    private String completionResult;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNoticeNo() { return noticeNo; }
    public void setNoticeNo(String noticeNo) { this.noticeNo = noticeNo; }
    public Integer getNoticeType() { return noticeType; }
    public void setNoticeType(Integer noticeType) { this.noticeType = noticeType; }
    public Long getTargetOperatorId() { return targetOperatorId; }
    public void setTargetOperatorId(Long targetOperatorId) { this.targetOperatorId = targetOperatorId; }
    public Long getTargetDeviceId() { return targetDeviceId; }
    public void setTargetDeviceId(Long targetDeviceId) { this.targetDeviceId = targetDeviceId; }
    public Long getTargetStationId() { return targetStationId; }
    public void setTargetStationId(Long targetStationId) { this.targetStationId = targetStationId; }
    public String getIssueReason() { return issueReason; }
    public void setIssueReason(String issueReason) { this.issueReason = issueReason; }
    public String getRectificationRequirement() { return rectificationRequirement; }
    public void setRectificationRequirement(String rectificationRequirement) { this.rectificationRequirement = rectificationRequirement; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Long getIssuerId() { return issuerId; }
    public void setIssuerId(Long issuerId) { this.issuerId = issuerId; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { this.issueTime = issueTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCompletionResult() { return completionResult; }
    public void setCompletionResult(String completionResult) { this.completionResult = completionResult; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
