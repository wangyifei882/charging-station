package com.charging.station.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.station.entity.RectificationNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RectificationNoticeMapper extends BaseMapper<RectificationNotice> {
    List<RectificationNotice> selectByTargetOperatorId(@Param("operatorId") Long operatorId);
    List<RectificationNotice> selectByStatus(@Param("status") Integer status);
    List<RectificationNotice> selectByNoticeType(@Param("noticeType") Integer noticeType);
}
