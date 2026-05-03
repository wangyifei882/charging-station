package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OperatorMapper extends BaseMapper<Operator> {
    List<Operator> selectByQualificationStatus(@Param("status") Integer status);
    List<Operator> selectByRatingGrade(@Param("grade") String grade);
    Map<String, Object> selectOperatorStats(@Param("operatorId") Long operatorId);
    BigDecimal selectTotalSubsidyAmount(@Param("operatorId") Long operatorId);
}
