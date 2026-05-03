package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.OperatorMonthlyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperatorMonthlyStatsMapper extends BaseMapper<OperatorMonthlyStats> {
    List<OperatorMonthlyStats> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<OperatorMonthlyStats> selectByMonth(@Param("month") String month);
}
