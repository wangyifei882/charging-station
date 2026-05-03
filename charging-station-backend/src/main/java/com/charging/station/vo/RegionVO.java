package com.charging.station.vo;

public class RegionVO {
    private String regionCode;
    private String regionName;
    private Integer regionLevel;
    private java.util.List<RegionVO> children;

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public Integer getRegionLevel() { return regionLevel; }
    public void setRegionLevel(Integer regionLevel) { this.regionLevel = regionLevel; }
    public java.util.List<RegionVO> getChildren() { return children; }
    public void setChildren(java.util.List<RegionVO> children) { this.children = children; }
}
