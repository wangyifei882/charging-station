package com.charging.station.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.dto.FaultTicketDTO;
import com.charging.station.entity.Device;
import com.charging.station.entity.FaultTicket;
import com.charging.station.mapper.DeviceMapper;
import com.charging.station.mapper.FaultTicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaultTicketService {
    @Autowired
    private FaultTicketMapper faultTicketMapper;
    
    @Autowired
    private DeviceMapper deviceMapper;

    public Page<FaultTicketDTO> getTicketPage(int page, int size, Integer status, Long deviceId) {
        Page<FaultTicket> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FaultTicket> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(FaultTicket::getStatus, status);
        }
        if (deviceId != null) {
            wrapper.eq(FaultTicket::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(FaultTicket::getCreateTime);
        Page<FaultTicket> ticketPage = faultTicketMapper.selectPage(pageParam, wrapper);
        
        // 转换为 DTO 并填充设备信息
        List<FaultTicketDTO> dtoList = ticketPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        Page<FaultTicketDTO> dtoPage = new Page<>(ticketPage.getCurrent(), ticketPage.getSize(), ticketPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
    
    private FaultTicketDTO convertToDTO(FaultTicket ticket) {
        FaultTicketDTO dto = new FaultTicketDTO();
        dto.setId(ticket.getId());
        dto.setTicketNo(ticket.getTicketNo());
        dto.setDeviceId(ticket.getDeviceId());
        dto.setStationId(ticket.getStationId());
        dto.setFaultType(ticket.getFaultType());
        dto.setFaultTypeName(getFaultTypeName(ticket.getFaultType()));
        dto.setFaultDescription(ticket.getFaultDescription());
        dto.setFaultImages(ticket.getFaultImages());
        dto.setReporterId(ticket.getReporterId());
        dto.setStatus(ticket.getStatus());
        dto.setStatusName(getStatusName(ticket.getStatus()));
        dto.setHandlerId(ticket.getHandlerId());
        dto.setHandlerCompany(ticket.getHandlerCompany());
        dto.setStartTime(ticket.getStartTime());
        dto.setEndTime(ticket.getEndTime());
        dto.setExpectedFinishTime(ticket.getExpectedFinishTime());
        dto.setSolution(ticket.getSolution());
        dto.setRemark(ticket.getRemark());
        dto.setCreateTime(ticket.getCreateTime());
        dto.setUpdateTime(ticket.getUpdateTime());
        
        // 查询设备信息
        if (ticket.getDeviceId() != null) {
            Device device = deviceMapper.selectById(ticket.getDeviceId());
            if (device != null) {
                dto.setDeviceCode(device.getDeviceCode());
                dto.setDeviceName(device.getDeviceName());
            }
        }
        
        return dto;
    }
    
    private String getFaultTypeName(Integer faultType) {
        if (faultType == null) return "其他";
        switch (faultType) {
            case 1: return "硬件故障";
            case 2: return "软件故障";
            case 3: return "通信故障";
            case 4: return "电源故障";
            case 5: return "环境因素";
            default: return "其他";
        }
    }
    
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已完成";
            case 3: return "已关闭";
            default: return "未知";
        }
    }

    public void createTicket(FaultTicket ticket) {
        String ticketNo = "FT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        ticket.setTicketNo(ticketNo);
        ticket.setStatus(0);
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        faultTicketMapper.insert(ticket);
    }

    public FaultTicket getTicketById(Long id) {
        return faultTicketMapper.selectById(id);
    }

    public void updateTicketStatus(Long id, Integer status) {
        FaultTicket ticket = new FaultTicket();
        ticket.setId(id);
        ticket.setStatus(status);
        ticket.setUpdateTime(LocalDateTime.now());
        faultTicketMapper.updateById(ticket);
    }

    public void completeTicket(Long id, String solution, String attachmentUrls) {
        FaultTicket ticket = new FaultTicket();
        ticket.setId(id);
        ticket.setStatus(2);
        ticket.setSolution(solution);
        ticket.setAttachmentUrls(attachmentUrls);
        ticket.setEndTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        faultTicketMapper.updateById(ticket);
    }
}
