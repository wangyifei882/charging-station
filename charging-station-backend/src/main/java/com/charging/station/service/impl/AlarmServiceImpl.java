package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.Alarm;
import com.charging.station.entity.AlarmHandlingRecord;
import com.charging.station.dto.AlarmHandleRequest;
import com.charging.station.mapper.AlarmMapper;
import com.charging.station.service.AlarmService;
import com.charging.station.service.AlarmHandlingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements AlarmService {

    @Autowired
    private AlarmHandlingRecordService alarmHandlingRecordService;

    @Override
    public IPage<Alarm> pageByCondition(int page, int size, Integer alarmLevel, Integer status, String regionCode, Long operatorId) {
        Page<Alarm> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        if (alarmLevel != null) {
            wrapper.eq(Alarm::getAlarmLevel, alarmLevel);
        }
        if (status != null) {
            wrapper.eq(Alarm::getStatus, status);
        }
        if (regionCode != null) {
            wrapper.eq(Alarm::getRegionCode, regionCode);
        }
        if (operatorId != null) {
            wrapper.eq(Alarm::getOperatorId, operatorId);
        }
        wrapper.orderByDesc(Alarm::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public Long getUnreadAlarmCount(Integer level) {
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alarm::getStatus, 0);
        if (level != null) {
            wrapper.eq(Alarm::getAlarmLevel, level);
        }
        return count(wrapper);
    }
}
