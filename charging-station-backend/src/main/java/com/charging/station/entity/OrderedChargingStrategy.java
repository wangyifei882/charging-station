package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("ordered_charging_strategy")
public class OrderedChargingStrategy {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String strategyNo;
    private Long stationId;
    private String stationName;
    private Long operatorId;
    private Integer strategyType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal targetLoadShift;
    private BigDecimal actualLoadShift;
    private BigDecimal executionRate;
    private BigDecimal valleyChargeRatioIncrease;
    private Integer status;
    private String regionCode;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStrategyNo() { return strategyNo; }
    public void setStrategyNo(String strategyNo) { this.strategyNo = strategyNo; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Integer getStrategyType() { return strategyType; }
    public void setStrategyType(Integer strategyType) { this.strategyType = strategyType; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTargetLoadShift() { return targetLoadShift; }
    public void setTargetLoadShift(BigDecimal targetLoadShift) { this.targetLoadShift = targetLoadShift; }
    public BigDecimal getActualLoadShift() { return actualLoadShift; }
    public void setActualLoadShift(BigDecimal actualLoadShift) { this.actualLoadShift = actualLoadShift; }
    public BigDecimal getExecutionRate() { return executionRate; }
    public void setExecutionRate(BigDecimal executionRate) { this.executionRate = executionRate; }
    public BigDecimal getValleyChargeRatioIncrease() { return valleyChargeRatioIncrease; }
    public void setValleyChargeRatioIncrease(BigDecimal valleyChargeRatioIncrease) { this.valleyChargeRatioIncrease = valleyChargeRatioIncrease; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
