package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("device")
public class Device {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceCode;
    private String deviceName;
    private Long typeId;
    private Long stationId;
    private String areaCode;
    private String position;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String firmwareVersion;
    private BigDecimal powerRating;
    private BigDecimal voltageRating;
    private LocalDate commissionDate;
    private LocalDate warrantyExpiryDate;
    private Integer maintenanceCycleDays;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private Integer status;
    private BigDecimal realTimePower;
    private BigDecimal realTimeCurrent;
    private BigDecimal realTimeVoltage;
    private BigDecimal totalEnergy;
    private String regionCode;
    private Long operatorId;
    private LocalDateTime lastCommTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    public BigDecimal getPowerRating() { return powerRating; }
    public void setPowerRating(BigDecimal powerRating) { this.powerRating = powerRating; }
    public BigDecimal getVoltageRating() { return voltageRating; }
    public void setVoltageRating(BigDecimal voltageRating) { this.voltageRating = voltageRating; }
    public LocalDate getCommissionDate() { return commissionDate; }
    public void setCommissionDate(LocalDate commissionDate) { this.commissionDate = commissionDate; }
    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }
    public Integer getMaintenanceCycleDays() { return maintenanceCycleDays; }
    public void setMaintenanceCycleDays(Integer maintenanceCycleDays) { this.maintenanceCycleDays = maintenanceCycleDays; }
    public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public BigDecimal getRealTimePower() { return realTimePower; }
    public void setRealTimePower(BigDecimal realTimePower) { this.realTimePower = realTimePower; }
    public BigDecimal getRealTimeCurrent() { return realTimeCurrent; }
    public void setRealTimeCurrent(BigDecimal realTimeCurrent) { this.realTimeCurrent = realTimeCurrent; }
    public BigDecimal getRealTimeVoltage() { return realTimeVoltage; }
    public void setRealTimeVoltage(BigDecimal realTimeVoltage) { this.realTimeVoltage = realTimeVoltage; }
    public BigDecimal getTotalEnergy() { return totalEnergy; }
    public void setTotalEnergy(BigDecimal totalEnergy) { this.totalEnergy = totalEnergy; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public LocalDateTime getLastCommTime() { return lastCommTime; }
    public void setLastCommTime(LocalDateTime lastCommTime) { this.lastCommTime = lastCommTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
