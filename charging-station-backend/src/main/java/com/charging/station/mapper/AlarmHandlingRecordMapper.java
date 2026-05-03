package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.AlarmHandlingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AlarmHandlingRecordMapper extends BaseMapper<AlarmHandlingRecord> {
    List<AlarmHandlingRecord> selectByAlarmId(@Param("alarmId") Long alarmId);
    List<AlarmHandlingRecord> selectByHandlerId(@Param("handlerId") Long handlerId);
}
