package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.Region;
import com.charging.station.mapper.RegionMapper;
import com.charging.station.service.RegionService;
import com.charging.station.vo.RegionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<RegionVO> getRegionTree() {
        List<Region> allRegions = list(new LambdaQueryWrapper<Region>().eq(Region::getStatus, 1).orderByAsc(Region::getSortOrder));
        return buildTree(allRegions, null);
    }

    @Override
    public List<Region> getChildrenByCode(String parentCode) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        if (parentCode == null) {
            wrapper.isNull(Region::getParentCode);
        } else {
            wrapper.eq(Region::getParentCode, parentCode);
        }
        return list(wrapper);
    }

    @Override
    public List<String> getSubRegionCodes(String regionCode) {
        List<String> codes = new java.util.ArrayList<>();
        codes.add(regionCode);
        collectSubRegionCodes(regionCode, codes);
        return codes;
    }

    private List<RegionVO> buildTree(List<Region> regions, String parentCode) {
        return regions.stream()
            .filter(r -> (parentCode == null && r.getParentCode() == null) || (parentCode != null && parentCode.equals(r.getParentCode())))
            .map(r -> {
                RegionVO vo = new RegionVO();
                BeanUtils.copyProperties(r, vo);
                vo.setChildren(buildTree(regions, r.getRegionCode()));
                return vo;
            })
            .collect(Collectors.toList());
    }

    private void collectSubRegionCodes(String regionCode, List<String> codes) {
        List<Region> children = getChildrenByCode(regionCode);
        for (Region child : children) {
            codes.add(child.getRegionCode());
            collectSubRegionCodes(child.getRegionCode(), codes);
        }
    }
}
