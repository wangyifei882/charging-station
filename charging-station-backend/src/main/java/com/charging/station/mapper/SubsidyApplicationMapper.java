package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.SubsidyApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubsidyApplicationMapper extends BaseMapper<SubsidyApplication> {
    List<SubsidyApplication> selectByAuditStatus(@Param("auditStatus") Integer auditStatus);
    List<SubsidyApplication> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<SubsidyApplication> selectByCurrentAuditStage(@Param("stage") Integer stage);
}
