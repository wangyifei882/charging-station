package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.DispatchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DispatchHistoryMapper extends BaseMapper<DispatchHistory> {
    List<DispatchHistory> selectByTaskId(@Param("taskId") Long taskId);
    List<DispatchHistory> selectByStationId(@Param("stationId") Long stationId);
}
