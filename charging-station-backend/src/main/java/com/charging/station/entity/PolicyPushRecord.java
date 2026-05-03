package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("policy_push_record")
public class PolicyPushRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long policyId;
    private Long operatorId;
    private Integer readStatus;
    private LocalDateTime readTime;
    private LocalDateTime pushTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Integer getReadStatus() { return readStatus; }
    public void setReadStatus(Integer readStatus) { this.readStatus = readStatus; }
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    public LocalDateTime getPushTime() { return pushTime; }
    public void setPushTime(LocalDateTime pushTime) { this.pushTime = pushTime; }
}
