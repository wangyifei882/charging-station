package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.V2gActivityParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface V2gActivityParticipantMapper extends BaseMapper<V2gActivityParticipant> {
    List<V2gActivityParticipant> selectByActivityId(@Param("activityId") Long activityId);
    List<V2gActivityParticipant> selectByOperatorId(@Param("operatorId") Long operatorId);
    BigDecimal sumDischargeEnergyByActivityId(@Param("activityId") Long activityId);
}
