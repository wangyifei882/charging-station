package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.FaultTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FaultTicketMapper extends BaseMapper<FaultTicket> {
}
