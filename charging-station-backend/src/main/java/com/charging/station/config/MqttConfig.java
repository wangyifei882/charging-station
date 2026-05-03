package com.charging.station.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    
    private Broker broker;
    private Credentials credentials;
    private Topics topics;
    private Subscription subscription;
    
    @Data
    public static class Broker {
        private String url;
        private String clientId;
    }
    
    @Data
    public static class Credentials {
        private String username;
        private String password;
    }
    
    @Data
    public static class Topics {
        private String realTimeData;
        private String statusData;
        private String lifeCycle;
        private String cmdReply;
    }
    
    @Data
    public static class Subscription {
        private String subKey;
        private int qos;
        private int connectionTimeout;
        private int keepAliveInterval;
        private boolean cleanSession;
        private boolean automaticReconnect;
    }
    
    public String getRealTimeDataTopic(String nodeId) {
        return topics.getRealTimeData()
                .replace("${subKey}", subscription.getSubKey())
                .replace("${nodeId}", nodeId);
    }
    
    public String getStatusDataTopic(String nodeId) {
        return topics.getStatusData()
                .replace("${subKey}", subscription.getSubKey())
                .replace("${nodeId}", nodeId);
    }
    
    public String getLifeCycleTopic(String nodeId) {
        return topics.getLifeCycle()
                .replace("${subKey}", subscription.getSubKey())
                .replace("${nodeId}", nodeId);
    }
    
    public String getCmdReplyTopic(String nodeId) {
        return topics.getCmdReply()
                .replace("${subKey}", subscription.getSubKey())
                .replace("${nodeId}", nodeId);
    }
}
