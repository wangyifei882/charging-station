package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.OrderedChargingStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderedChargingStrategyMapper extends BaseMapper<OrderedChargingStrategy> {
    List<OrderedChargingStrategy> selectByStationId(@Param("stationId") Long stationId);
    List<OrderedChargingStrategy> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<OrderedChargingStrategy> selectByStatus(@Param("status") Integer status);
}
