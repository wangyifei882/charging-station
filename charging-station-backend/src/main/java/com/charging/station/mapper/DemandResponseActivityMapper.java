package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.DemandResponseActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DemandResponseActivityMapper extends BaseMapper<DemandResponseActivity> {
    List<DemandResponseActivity> selectByResponseType(@Param("responseType") Integer responseType);
    List<DemandResponseActivity> selectByStatus(@Param("status") Integer status);
}
