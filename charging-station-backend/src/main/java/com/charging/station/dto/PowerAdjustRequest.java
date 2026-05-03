package com.charging.station.dto;

import java.math.BigDecimal;

public class PowerAdjustRequest {
    private BigDecimal maxChargePower;
    private BigDecimal maxDischargePower;
    private String adjustReason;

    public BigDecimal getMaxChargePower() { return maxChargePower; }
    public void setMaxChargePower(BigDecimal maxChargePower) { this.maxChargePower = maxChargePower; }
    public BigDecimal getMaxDischargePower() { return maxDischargePower; }
    public void setMaxDischargePower(BigDecimal maxDischargePower) { this.maxDischargePower = maxDischargePower; }
    public String getAdjustReason() { return adjustReason; }
    public void setAdjustReason(String adjustReason) { this.adjustReason = adjustReason; }
}
