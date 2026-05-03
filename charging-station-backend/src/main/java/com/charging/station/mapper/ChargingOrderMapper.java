package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.ChargingOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChargingOrderMapper extends BaseMapper<ChargingOrder> {
}
