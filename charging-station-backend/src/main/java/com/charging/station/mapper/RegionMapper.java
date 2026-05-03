package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RegionMapper extends BaseMapper<Region> {
    List<Region> selectByParentCode(@Param("parentCode") String parentCode);
    List<Region> selectByLevel(@Param("level") Integer level);
    List<Region> selectTree();
    List<String> selectSubRegionCodes(@Param("regionCode") String regionCode);
}
