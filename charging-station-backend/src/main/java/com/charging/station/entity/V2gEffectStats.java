package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("v2g_effect_stats")
public class V2gEffectStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String statsPeriod;
    private Integer statsType;
    private BigDecimal totalPeakShaveEnergy;
    private BigDecimal totalValleyFillEnergy;
    private BigDecimal totalRenewableEnergy;
    private BigDecimal totalUserRevenue;
    private Integer v2gActivityCount;
    private Integer demandResponseCount;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatsPeriod() { return statsPeriod; }
    public void setStatsPeriod(String statsPeriod) { this.statsPeriod = statsPeriod; }
    public Integer getStatsType() { return statsType; }
    public void setStatsType(Integer statsType) { this.statsType = statsType; }
    public BigDecimal getTotalPeakShaveEnergy() { return totalPeakShaveEnergy; }
    public void setTotalPeakShaveEnergy(BigDecimal totalPeakShaveEnergy) { this.totalPeakShaveEnergy = totalPeakShaveEnergy; }
    public BigDecimal getTotalValleyFillEnergy() { return totalValleyFillEnergy; }
    public void setTotalValleyFillEnergy(BigDecimal totalValleyFillEnergy) { this.totalValleyFillEnergy = totalValleyFillEnergy; }
    public BigDecimal getTotalRenewableEnergy() { return totalRenewableEnergy; }
    public void setTotalRenewableEnergy(BigDecimal totalRenewableEnergy) { this.totalRenewableEnergy = totalRenewableEnergy; }
    public BigDecimal getTotalUserRevenue() { return totalUserRevenue; }
    public void setTotalUserRevenue(BigDecimal totalUserRevenue) { this.totalUserRevenue = totalUserRevenue; }
    public Integer getV2gActivityCount() { return v2gActivityCount; }
    public void setV2gActivityCount(Integer v2gActivityCount) { this.v2gActivityCount = v2gActivityCount; }
    public Integer getDemandResponseCount() { return demandResponseCount; }
    public void setDemandResponseCount(Integer demandResponseCount) { this.demandResponseCount = demandResponseCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
