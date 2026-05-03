package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.DispatchStationAllocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DispatchStationAllocationMapper extends BaseMapper<DispatchStationAllocation> {
    List<DispatchStationAllocation> selectByTaskId(@Param("taskId") Long taskId);
    List<DispatchStationAllocation> selectByStationId(@Param("stationId") Long stationId);
    List<DispatchStationAllocation> selectByExecutionStatus(@Param("executionStatus") Integer executionStatus);
}
