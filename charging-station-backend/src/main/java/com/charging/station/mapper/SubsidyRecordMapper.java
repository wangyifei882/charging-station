package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.SubsidyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubsidyRecordMapper extends BaseMapper<SubsidyRecord> {
    List<SubsidyRecord> selectByApplicationId(@Param("applicationId") Long applicationId);
    List<SubsidyRecord> selectByOperatorId(@Param("operatorId") Long operatorId);
    List<SubsidyRecord> selectByGrantBatchNo(@Param("batchNo") String batchNo);
}
