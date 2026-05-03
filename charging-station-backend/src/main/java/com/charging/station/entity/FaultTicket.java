package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("fault_ticket")
public class FaultTicket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ticketNo;
    private Long deviceId;
    private Long stationId;
    private Integer faultType;
    private String faultDescription;
    private String faultImages;
    private Long reporterId;
    private Integer status;
    private Long handlerId;
    private String handlerCompany;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedFinishTime;
    private String solution;
    private String onlineConsultRecord;
    private String attachmentUrls;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    public Integer getFaultType() { return faultType; }
    public void setFaultType(Integer faultType) { this.faultType = faultType; }
    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }
    public String getFaultImages() { return faultImages; }
    public void setFaultImages(String faultImages) { this.faultImages = faultImages; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }
    public String getHandlerCompany() { return handlerCompany; }
    public void setHandlerCompany(String handlerCompany) { this.handlerCompany = handlerCompany; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getExpectedFinishTime() { return expectedFinishTime; }
    public void setExpectedFinishTime(LocalDateTime expectedFinishTime) { this.expectedFinishTime = expectedFinishTime; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getOnlineConsultRecord() { return onlineConsultRecord; }
    public void setOnlineConsultRecord(String onlineConsultRecord) { this.onlineConsultRecord = onlineConsultRecord; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
