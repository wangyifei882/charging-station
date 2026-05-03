package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("operator_rating")
public class OperatorRating {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long operatorId;
    private String ratingPeriod;
    private Integer ratingType;
    private BigDecimal totalScore;
    private BigDecimal serviceQualityScore;
    private BigDecimal equipmentStatusScore;
    private BigDecimal complianceScore;
    private BigDecimal responseScore;
    private String grade;
    private String remark;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getRatingPeriod() { return ratingPeriod; }
    public void setRatingPeriod(String ratingPeriod) { this.ratingPeriod = ratingPeriod; }
    public Integer getRatingType() { return ratingType; }
    public void setRatingType(Integer ratingType) { this.ratingType = ratingType; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getServiceQualityScore() { return serviceQualityScore; }
    public void setServiceQualityScore(BigDecimal serviceQualityScore) { this.serviceQualityScore = serviceQualityScore; }
    public BigDecimal getEquipmentStatusScore() { return equipmentStatusScore; }
    public void setEquipmentStatusScore(BigDecimal equipmentStatusScore) { this.equipmentStatusScore = equipmentStatusScore; }
    public BigDecimal getComplianceScore() { return complianceScore; }
    public void setComplianceScore(BigDecimal complianceScore) { this.complianceScore = complianceScore; }
    public BigDecimal getResponseScore() { return responseScore; }
    public void setResponseScore(BigDecimal responseScore) { this.responseScore = responseScore; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
