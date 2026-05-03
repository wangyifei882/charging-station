package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
