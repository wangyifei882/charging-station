package com.charging.station.dto;

import java.math.BigDecimal;

public class OperatorParticipationRankDTO {
    private Integer rank;
    private Long operatorId;
    private String operatorName;
    private Integer responseCount;
    private BigDecimal totalPower;
    private BigDecimal totalEnergy;
    private BigDecimal responseRate;
    private BigDecimal avgResponseRate;
    private BigDecimal rewardAmount;

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public Integer getResponseCount() { return responseCount; }
    public void setResponseCount(Integer responseCount) { this.responseCount = responseCount; }
    public BigDecimal getTotalPower() { return totalPower; }
    public void setTotalPower(BigDecimal totalPower) { this.totalPower = totalPower; }
    public BigDecimal getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(BigDecimal totalEnergy) { this.totalEnergy = totalEnergy; }
    public BigDecimal getResponseRate() { return responseRate; }
    public void setResponseRate(BigDecimal responseRate) { this.responseRate = responseRate; }
    public BigDecimal getAvgResponseRate() { return avgResponseRate != null ? avgResponseRate : responseRate; }
    public void setAvgResponseRate(BigDecimal avgResponseRate) { this.avgResponseRate = avgResponseRate; }
    public BigDecimal getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(BigDecimal rewardAmount) { this.rewardAmount = rewardAmount; }
}
