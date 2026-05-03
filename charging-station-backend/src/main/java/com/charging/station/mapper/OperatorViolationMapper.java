package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.OperatorViolation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperatorViolationMapper extends BaseMapper<OperatorViolation> {
    List<OperatorViolation> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<OperatorViolation> selectByRectificationStatus(@Param("status") Integer status);
    Long countByOperatorId(@Param("operatorId") Long operatorId);
}
