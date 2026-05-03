package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.dto.AccessApplicationDTO;
import com.charging.station.dto.AuditRequest;
import com.charging.station.entity.AccessApplication;

public interface AccessApplicationService extends IService<AccessApplication> {
    IPage<AccessApplicationDTO> pageByCondition(int page, int size, Integer auditStatus, String keyword);
    void audit(Long id, AuditRequest request, Long auditorId);
}
