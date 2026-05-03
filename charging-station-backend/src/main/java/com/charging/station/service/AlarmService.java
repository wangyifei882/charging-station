package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Alarm;

public interface AlarmService extends IService<Alarm> {
    IPage<Alarm> pageByCondition(int page, int size, Integer alarmLevel, Integer status, String regionCode, Long operatorId);
    Long getUnreadAlarmCount(Integer level);
}
