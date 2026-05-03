package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.V2gActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface V2gActivityMapper extends BaseMapper<V2gActivity> {
    List<V2gActivity> selectByStatus(@Param("status") Integer status);
    List<V2gActivity> selectByRegionCode(@Param("regionCode") String regionCode);
}
