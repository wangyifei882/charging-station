package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.EmergencyDispatchCommand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EmergencyDispatchCommandMapper extends BaseMapper<EmergencyDispatchCommand> {
    List<EmergencyDispatchCommand> selectByStatus(@Param("status") Integer status);
    List<EmergencyDispatchCommand> selectByTargetOperatorId(@Param("operatorId") Long operatorId);
    List<EmergencyDispatchCommand> selectByCommandType(@Param("commandType") Integer commandType);
}
