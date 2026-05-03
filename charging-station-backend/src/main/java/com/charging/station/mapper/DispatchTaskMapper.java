package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.DispatchTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DispatchTaskMapper extends BaseMapper<DispatchTask> {
    List<DispatchTask> selectByStatus(@Param("status") Integer status);
    List<DispatchTask> selectByDispatchMode(@Param("dispatchMode") Integer dispatchMode);
    List<DispatchTask> selectByRegionCode(@Param("regionCode") String regionCode);
}
