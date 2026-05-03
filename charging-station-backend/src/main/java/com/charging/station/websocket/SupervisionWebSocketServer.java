package com.charging.station.websocket;

import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/ws/supervision/{regionCode}")
public class SupervisionWebSocketServer {

    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<SupervisionWebSocketServer>> REGION_SESSIONS = new ConcurrentHashMap<>();
    
    private Session session;
    private String regionCode;

    @OnOpen
    public void onOpen(Session session, @PathParam("regionCode") String regionCode) {
        this.session = session;
        this.regionCode = regionCode;
        
        REGION_SESSIONS.computeIfAbsent(regionCode, k -> new CopyOnWriteArraySet<>()).add(this);
        
        sendMessage("欢迎连接监管平台WebSocket - 区域: " + regionCode);
    }

    @OnClose
    public void onClose() {
        CopyOnWriteArraySet<SupervisionWebSocketServer> sessions = REGION_SESSIONS.get(regionCode);
        if (sessions != null) {
            sessions.remove(this);
            if (sessions.isEmpty()) {
                REGION_SESSIONS.remove(regionCode);
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        sendMessage("收到消息: " + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastToRegion(String regionCode, String message) {
        CopyOnWriteArraySet<SupervisionWebSocketServer> sessions = REGION_SESSIONS.get(regionCode);
        if (sessions != null) {
            for (SupervisionWebSocketServer server : sessions) {
                server.sendMessage(message);
            }
        }
    }

    public static void broadcastToAll(String message) {
        for (CopyOnWriteArraySet<SupervisionWebSocketServer> sessions : REGION_SESSIONS.values()) {
            for (SupervisionWebSocketServer server : sessions) {
                server.sendMessage(message);
            }
        }
    }
}
