package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("dispatch_task")
public class DispatchTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskNo;
    private Integer dispatchMode;
    private Integer dispatchType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String regionCode;
    private BigDecimal totalChargePower;
    private BigDecimal totalDischargePower;
    private BigDecimal actualChargePower;
    private BigDecimal actualDischargePower;
    private BigDecimal executionRate;
    private Integer availableStationCount;
    private Integer overloadStationCount;
    private Integer gridConstraintStatus;
    private Integer status;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public Integer getDispatchMode() { return dispatchMode; }
    public void setDispatchMode(Integer dispatchMode) { this.dispatchMode = dispatchMode; }
    public Integer getDispatchType() { return dispatchType; }
    public void setDispatchType(Integer dispatchType) { this.dispatchType = dispatchType; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public BigDecimal getTotalChargePower() { return totalChargePower; }
    public void setTotalChargePower(BigDecimal totalChargePower) { this.totalChargePower = totalChargePower; }
    public BigDecimal getTotalDischargePower() { return totalDischargePower; }
    public void setTotalDischargePower(BigDecimal totalDischargePower) { this.totalDischargePower = totalDischargePower; }
    public BigDecimal getActualChargePower() { return actualChargePower; }
    public void setActualChargePower(BigDecimal actualChargePower) { this.actualChargePower = actualChargePower; }
    public BigDecimal getActualDischargePower() { return actualDischargePower; }
    public void setActualDischargePower(BigDecimal actualDischargePower) { this.actualDischargePower = actualDischargePower; }
    public BigDecimal getExecutionRate() { return executionRate; }
    public void setExecutionRate(BigDecimal executionRate) { this.executionRate = executionRate; }
    public Integer getAvailableStationCount() { return availableStationCount; }
    public void setAvailableStationCount(Integer availableStationCount) { this.availableStationCount = availableStationCount; }
    public Integer getOverloadStationCount() { return overloadStationCount; }
    public void setOverloadStationCount(Integer overloadStationCount) { this.overloadStationCount = overloadStationCount; }
    public Integer getGridConstraintStatus() { return gridConstraintStatus; }
    public void setGridConstraintStatus(Integer gridConstraintStatus) { this.gridConstraintStatus = gridConstraintStatus; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
