package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("operator_monthly_stats")
public class OperatorMonthlyStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long operatorId;
    private String statsMonth;
    private Integer stationCount;
    private Integer deviceCount;
    private BigDecimal totalEnergy;
    private BigDecimal totalRevenue;
    private BigDecimal v2gDischargeEnergy;
    private BigDecimal demandResponseEnergy;
    private Integer faultCount;
    private Integer complaintCount;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getStatsMonth() { return statsMonth; }
    public void setStatsMonth(String statsMonth) { this.statsMonth = statsMonth; }
    public Integer getStationCount() { return stationCount; }
    public void setStationCount(Integer stationCount) { this.stationCount = stationCount; }
    public Integer getDeviceCount() { return deviceCount; }
    public void setDeviceCount(Integer deviceCount) { this.deviceCount = deviceCount; }
    public BigDecimal getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(BigDecimal totalEnergy) { this.totalEnergy = totalEnergy; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getV2gDischargeEnergy() { return v2gDischargeEnergy; }
    public void setV2gDischargeEnergy(BigDecimal v2gDischargeEnergy) { this.v2gDischargeEnergy = v2gDischargeEnergy; }
    public BigDecimal getDemandResponseEnergy() { return demandResponseEnergy; }
    public void setDemandResponseEnergy(BigDecimal demandResponseEnergy) { this.demandResponseEnergy = demandResponseEnergy; }
    public Integer getFaultCount() { return faultCount; }
    public void setFaultCount(Integer faultCount) { this.faultCount = faultCount; }
    public Integer getComplaintCount() { return complaintCount; }
    public void setComplaintCount(Integer complaintCount) { this.complaintCount = complaintCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
