package com.charging.station.service;

import com.charging.station.entity.Alarm;
import com.charging.station.entity.DispatchTask;
import com.charging.station.entity.EmergencyDispatchCommand;
import com.charging.station.websocket.SupervisionWebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketPushService {

    public static final String EVENT_ALARM_NEW = "alarm:new";
    public static final String EVENT_DISPATCH_UPDATE = "dispatch:update";
    public static final String EVENT_EMERGENCY_COMMAND = "emergency:command";

    @Autowired
    private ObjectMapper objectMapper;

    public void pushNewAlarm(Alarm alarm, String regionCode) {
        Map<String, Object> message = new HashMap<>();
        message.put("event", EVENT_ALARM_NEW);
        message.put("data", alarm);
        String jsonMessage = toJsonString(message);
        
        if (regionCode != null) {
            SupervisionWebSocketServer.broadcastToRegion(regionCode, jsonMessage);
        } else {
            SupervisionWebSocketServer.broadcastToAll(jsonMessage);
        }
    }

    public void pushDispatchUpdate(DispatchTask task, String regionCode) {
        Map<String, Object> message = new HashMap<>();
        message.put("event", EVENT_DISPATCH_UPDATE);
        message.put("data", task);
        String jsonMessage = toJsonString(message);
        
        if (regionCode != null) {
            SupervisionWebSocketServer.broadcastToRegion(regionCode, jsonMessage);
        } else {
            SupervisionWebSocketServer.broadcastToAll(jsonMessage);
        }
    }

    public void pushEmergencyCommand(EmergencyDispatchCommand command, String regionCode) {
        Map<String, Object> message = new HashMap<>();
        message.put("event", EVENT_EMERGENCY_COMMAND);
        message.put("data", command);
        String jsonMessage = toJsonString(message);
        
        if (regionCode != null) {
            SupervisionWebSocketServer.broadcastToRegion(regionCode, jsonMessage);
        } else {
            SupervisionWebSocketServer.broadcastToAll(jsonMessage);
        }
    }

    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }
}
