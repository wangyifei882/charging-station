package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.OperatorRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperatorRatingMapper extends BaseMapper<OperatorRating> {
    List<OperatorRating> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<OperatorRating> selectByGrade(@Param("grade") String grade);
    List<OperatorRating> selectByPeriod(@Param("period") String period);
}
