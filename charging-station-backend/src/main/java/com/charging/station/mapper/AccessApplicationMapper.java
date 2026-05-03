package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.AccessApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AccessApplicationMapper extends BaseMapper<AccessApplication> {
    List<AccessApplication> selectByAuditStatus(@Param("auditStatus") Integer auditStatus);
    List<AccessApplication> selectByOperatorId(@Param("operatorId") Long operatorId);
}
