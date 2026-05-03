package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("operator")
public class Operator {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operatorCode;
    private String operatorName;
    private String creditCode;
    private String legalPerson;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String registrationRegion;
    private String address;
    private String businessScope;
    private Integer accessStationCount;
    private Integer qualificationStatus;
    private LocalDate qualificationValidDate;
    private String ratingGrade;
    private BigDecimal ratingScore;
    private Integer violationCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperatorCode() { return operatorCode; }
    public void setOperatorCode(String operatorCode) { this.operatorCode = operatorCode; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getLegalPerson() { return legalPerson; }
    public void setLegalPerson(String legalPerson) { this.legalPerson = legalPerson; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getRegistrationRegion() { return registrationRegion; }
    public void setRegistrationRegion(String registrationRegion) { this.registrationRegion = registrationRegion; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBusinessScope() { return businessScope; }
    public void setBusinessScope(String businessScope) { this.businessScope = businessScope; }
    public Integer getAccessStationCount() { return accessStationCount; }
    public void setAccessStationCount(Integer accessStationCount) { this.accessStationCount = accessStationCount; }
    public Integer getQualificationStatus() { return qualificationStatus; }
    public void setQualificationStatus(Integer qualificationStatus) { this.qualificationStatus = qualificationStatus; }
    public LocalDate getQualificationValidDate() { return qualificationValidDate; }
    public void setQualificationValidDate(LocalDate qualificationValidDate) { this.qualificationValidDate = qualificationValidDate; }
    public String getRatingGrade() { return ratingGrade; }
    public void setRatingGrade(String ratingGrade) { this.ratingGrade = ratingGrade; }
    public BigDecimal getRatingScore() { return ratingScore; }
    public void setRatingScore(BigDecimal ratingScore) { this.ratingScore = ratingScore; }
    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
