package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("dispatch_station_allocation")
public class DispatchStationAllocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long stationId;
    private String stationName;
    private Long operatorId;
    private String regionCode;
    private BigDecimal maxChargePower;
    private BigDecimal maxDischargePower;
    private BigDecimal totalPowerLimit;
    private BigDecimal actualChargePower;
    private BigDecimal actualDischargePower;
    private BigDecimal executionDeviation;
    private Integer executionStatus;
    private BigDecimal transformerCapacity;
    private BigDecimal transformerLimit;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public BigDecimal getMaxChargePower() { return maxChargePower; }
    public void setMaxChargePower(BigDecimal maxChargePower) { this.maxChargePower = maxChargePower; }
    public BigDecimal getMaxDischargePower() { return maxDischargePower; }
    public void setMaxDischargePower(BigDecimal maxDischargePower) { this.maxDischargePower = maxDischargePower; }
    public BigDecimal getTotalPowerLimit() { return totalPowerLimit; }
    public void setTotalPowerLimit(BigDecimal totalPowerLimit) { this.totalPowerLimit = totalPowerLimit; }
    public BigDecimal getActualChargePower() { return actualChargePower; }
    public void setActualChargePower(BigDecimal actualChargePower) { this.actualChargePower = actualChargePower; }
    public BigDecimal getActualDischargePower() { return actualDischargePower; }
    public void setActualDischargePower(BigDecimal actualDischargePower) { this.actualDischargePower = actualDischargePower; }
    public BigDecimal getExecutionDeviation() { return executionDeviation; }
    public void setExecutionDeviation(BigDecimal executionDeviation) { this.executionDeviation = executionDeviation; }
    public Integer getExecutionStatus() { return executionStatus; }
    public void setExecutionStatus(Integer executionStatus) { this.executionStatus = executionStatus; }
    public BigDecimal getTransformerCapacity() { return transformerCapacity; }
    public void setTransformerCapacity(BigDecimal transformerCapacity) { this.transformerCapacity = transformerCapacity; }
    public BigDecimal getTransformerLimit() { return transformerLimit; }
    public void setTransformerLimit(BigDecimal transformerLimit) { this.transformerLimit = transformerLimit; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
