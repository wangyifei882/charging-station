package com.charging.station.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("alarm_handling_record")
public class AlarmHandlingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long alarmId;
    private Integer handlingType;
    private Long handlerId;
    private String handlerName;
    private String handlingContent;
    private String notifyMethod;
    private LocalDateTime notifyTime;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAlarmId() { return alarmId; }
    public void setAlarmId(Long alarmId) { this.alarmId = alarmId; }
    public Integer getHandlingType() { return handlingType; }
    public void setHandlingType(Integer handlingType) { this.handlingType = handlingType; }
    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }
    public String getHandlerName() { return handlerName; }
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }
    public String getHandlingContent() { return handlingContent; }
    public void setHandlingContent(String handlingContent) { this.handlingContent = handlingContent; }
    public String getNotifyMethod() { return notifyMethod; }
    public void setNotifyMethod(String notifyMethod) { this.notifyMethod = notifyMethod; }
    public LocalDateTime getNotifyTime() { return notifyTime; }
    public void setNotifyTime(LocalDateTime notifyTime) { this.notifyTime = notifyTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
