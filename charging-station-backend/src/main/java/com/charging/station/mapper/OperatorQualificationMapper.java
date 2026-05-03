package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.OperatorQualification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperatorQualificationMapper extends BaseMapper<OperatorQualification> {
    List<OperatorQualification> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<OperatorQualification> selectByAuditStatus(@Param("auditStatus") Integer auditStatus);
}
