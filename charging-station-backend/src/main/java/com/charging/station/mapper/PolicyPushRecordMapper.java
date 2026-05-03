package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.PolicyPushRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PolicyPushRecordMapper extends BaseMapper<PolicyPushRecord> {
    List<PolicyPushRecord> selectByPolicyId(@Param("policyId") Long policyId);
    List<PolicyPushRecord> selectByOperatorId(@Param("operatorId") Long operatorId);
}
