package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("operator_violation")
public class OperatorViolation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String violationNo;
    private Long operatorId;
    private Integer violationType;
    private String violationContent;
    private Integer penaltyType;
    private BigDecimal penaltyAmount;
    private LocalDate rectificationDeadline;
    private Integer rectificationStatus;
    private String rectificationResult;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getViolationNo() { return violationNo; }
    public void setViolationNo(String violationNo) { this.violationNo = violationNo; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Integer getViolationType() { return violationType; }
    public void setViolationType(Integer violationType) { this.violationType = violationType; }
    public String getViolationContent() { return violationContent; }
    public void setViolationContent(String violationContent) { this.violationContent = violationContent; }
    public Integer getPenaltyType() { return penaltyType; }
    public void setPenaltyType(Integer penaltyType) { this.penaltyType = penaltyType; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public LocalDate getRectificationDeadline() { return rectificationDeadline; }
    public void setRectificationDeadline(LocalDate rectificationDeadline) { this.rectificationDeadline = rectificationDeadline; }
    public Integer getRectificationStatus() { return rectificationStatus; }
    public void setRectificationStatus(Integer rectificationStatus) { this.rectificationStatus = rectificationStatus; }
    public String getRectificationResult() { return rectificationResult; }
    public void setRectificationResult(String rectificationResult) { this.rectificationResult = rectificationResult; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
