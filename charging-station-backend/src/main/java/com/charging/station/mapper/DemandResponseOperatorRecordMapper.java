package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.DemandResponseOperatorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DemandResponseOperatorRecordMapper extends BaseMapper<DemandResponseOperatorRecord> {
    List<DemandResponseOperatorRecord> selectByActivityId(@Param("activityId") Long activityId);
    List<DemandResponseOperatorRecord> selectByOperatorId(@Param("operatorId") Long operatorId);
}
