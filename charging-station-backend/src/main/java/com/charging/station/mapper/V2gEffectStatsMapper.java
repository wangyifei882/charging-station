package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.V2gEffectStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface V2gEffectStatsMapper extends BaseMapper<V2gEffectStats> {
    List<V2gEffectStats> selectByStatsType(@Param("statsType") Integer statsType);
    V2gEffectStats selectByPeriodAndType(@Param("period") String period, @Param("type") Integer type);
}
