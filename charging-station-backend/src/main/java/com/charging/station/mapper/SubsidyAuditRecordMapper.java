package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.SubsidyAuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubsidyAuditRecordMapper extends BaseMapper<SubsidyAuditRecord> {
    List<SubsidyAuditRecord> selectByApplicationId(@Param("applicationId") Long applicationId);
    List<SubsidyAuditRecord> selectByAuditorId(@Param("auditorId") Long auditorId);
}
