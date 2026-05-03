package com.charging.station.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iot.platform")
public class IotPlatformConfig {
    
    private String baseUrl;
    private String appId;
    private String appSecret;
    
    private SyncConfig sync = new SyncConfig();
    private WebSocketConfig websocket = new WebSocketConfig();
    private HttpClientConfig http = new HttpClientConfig();
    
    public static class SyncConfig {
        private Integer interval = 10;
        private Integer batchSize = 50;
        private Integer historyInterval = 300;
        
        public Integer getInterval() { return interval; }
        public void setInterval(Integer interval) { this.interval = interval; }
        public Integer getBatchSize() { return batchSize; }
        public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
        public Integer getHistoryInterval() { return historyInterval; }
        public void setHistoryInterval(Integer historyInterval) { this.historyInterval = historyInterval; }
    }
    
    public static class WebSocketConfig {
        private Boolean enabled = true;
        private Integer heartbeatInterval = 30;
        private Integer reconnectInterval = 10;
        private Integer maxReconnectAttempts = 5;
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        public Integer getHeartbeatInterval() { return heartbeatInterval; }
        public void setHeartbeatInterval(Integer heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
        public Integer getReconnectInterval() { return reconnectInterval; }
        public void setReconnectInterval(Integer reconnectInterval) { this.reconnectInterval = reconnectInterval; }
        public Integer getMaxReconnectAttempts() { return maxReconnectAttempts; }
        public void setMaxReconnectAttempts(Integer maxReconnectAttempts) { this.maxReconnectAttempts = maxReconnectAttempts; }
    }
    
    public static class HttpClientConfig {
        private Integer connectTimeout = 5000;
        private Integer readTimeout = 10000;
        private Integer maxConnections = 100;
        private Integer rateLimit = 100;
        
        public Integer getConnectTimeout() { return connectTimeout; }
        public void setConnectTimeout(Integer connectTimeout) { this.connectTimeout = connectTimeout; }
        public Integer getReadTimeout() { return readTimeout; }
        public void setReadTimeout(Integer readTimeout) { this.readTimeout = readTimeout; }
        public Integer getMaxConnections() { return maxConnections; }
        public void setMaxConnections(Integer maxConnections) { this.maxConnections = maxConnections; }
        public Integer getRateLimit() { return rateLimit; }
        public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }
    }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public SyncConfig getSync() { return sync; }
    public void setSync(SyncConfig sync) { this.sync = sync; }
    public WebSocketConfig getWebsocket() { return websocket; }
    public void setWebsocket(WebSocketConfig websocket) { this.websocket = websocket; }
    public HttpClientConfig getHttp() { return http; }
    public void setHttp(HttpClientConfig http) { this.http = http; }
}
