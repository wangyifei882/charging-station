package com.charging.station.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.AlarmHandlingRecord;
import com.charging.station.mapper.AlarmHandlingRecordMapper;
import com.charging.station.service.AlarmHandlingRecordService;
import org.springframework.stereotype.Service;

@Service
public class AlarmHandlingRecordServiceImpl extends ServiceImpl<AlarmHandlingRecordMapper, AlarmHandlingRecord> implements AlarmHandlingRecordService {
}
