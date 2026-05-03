package com.charging.station.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Region;
import com.charging.station.vo.RegionVO;
import java.util.List;

public interface RegionService extends IService<Region> {
    List<RegionVO> getRegionTree();
    List<Region> getChildrenByCode(String parentCode);
    List<String> getSubRegionCodes(String regionCode);
}
