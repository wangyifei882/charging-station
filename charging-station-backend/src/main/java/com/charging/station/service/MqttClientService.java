package com.charging.station.service;

import com.charging.station.config.MqttConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MqttClientService implements MqttCallback {
    
    @Autowired
    private MqttConfig mqttConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MqttDataHandlerService dataHandlerService;
    
    private MqttClient mqttClient;
    
    // 存储设备实时数据
    private final Map<String, Map<String, Object>> deviceRealTimeData = new ConcurrentHashMap<>();
    
    // 存储设备状态
    private final Map<String, String> deviceStatus = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        connect();
    }
    
    @PreDestroy
    public void destroy() {
        disconnect();
    }
    
    public void connect() {
        try {
            String brokerUrl = mqttConfig.getBroker().getUrl();
            String clientId = mqttConfig.getBroker().getClientId();
            
            log.info("正在连接 MQTT Broker: {}", brokerUrl);
            log.info("Client ID: {}", clientId);
            
            mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttConfig.getCredentials().getUsername());
            options.setPassword(mqttConfig.getCredentials().getPassword().toCharArray());
            options.setConnectionTimeout(mqttConfig.getSubscription().getConnectionTimeout());
            options.setKeepAliveInterval(mqttConfig.getSubscription().getKeepAliveInterval());
            options.setCleanSession(mqttConfig.getSubscription().isCleanSession());
            options.setAutomaticReconnect(mqttConfig.getSubscription().isAutomaticReconnect());
            
            mqttClient.setCallback(this);
            mqttClient.connect(options);
            
            log.info("MQTT 连接成功！");
            
            // 订阅通配符主题，接收所有设备的数据
            subscribeToAllDevices();
            
        } catch (MqttException e) {
            log.error("MQTT 连接失败: {}", e.getMessage(), e);
        }
    }
    
    private void subscribeToAllDevices() {
        try {
            String subKey = mqttConfig.getSubscription().getSubKey();
            int qos = mqttConfig.getSubscription().getQos();
            
            // 订阅实时数据主题 (使用通配符 # 订阅所有设备)
            String realTimeTopic = "iot/device/" + subKey + "/realTimeData/+";
            mqttClient.subscribe(realTimeTopic, qos);
            log.info("已订阅实时数据主题: {}", realTimeTopic);
            
            // 订阅在线状态主题
            String statusTopic = "iot/device/" + subKey + "/statusData/+";
            mqttClient.subscribe(statusTopic, qos);
            log.info("已订阅状态数据主题: {}", statusTopic);
            
            // 订阅生命周期主题
            String lifeCycleTopic = "iot/device/" + subKey + "/lifeCycle/+";
            mqttClient.subscribe(lifeCycleTopic, qos);
            log.info("已订阅生命周期主题: {}", lifeCycleTopic);
            
            // 订阅命令回复主题
            String cmdReplyTopic = "iot/device/" + subKey + "/cmdReply/+";
            mqttClient.subscribe(cmdReplyTopic, qos);
            log.info("已订阅命令回复主题: {}", cmdReplyTopic);
            
        } catch (MqttException e) {
            log.error("订阅主题失败: {}", e.getMessage(), e);
        }
    }
    
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
                log.info("MQTT 连接已断开");
            }
        } catch (MqttException e) {
            log.error("断开 MQTT 连接失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT 连接丢失: {}", cause.getMessage());
        // 自动重连会在 options 中配置
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        log.debug("收到 MQTT 消息 - Topic: {}, Payload: {}", topic, payload);
        
        try {
            // 解析 topic 提取 nodeId
            String nodeId = extractNodeId(topic);
            if (nodeId == null) {
                log.warn("无法从 topic 提取 nodeId: {}", topic);
                return;
            }
            
            // 解析消息内容
            Map<String, Object> data = objectMapper.readValue(payload, Map.class);
            
            // 根据 topic 类型处理数据
            if (topic.contains("/realTimeData/")) {
                handleRealTimeData(nodeId, data);
            } else if (topic.contains("/statusData/")) {
                handleStatusData(nodeId, data);
            } else if (topic.contains("/lifeCycle/")) {
                handleLifeCycleData(nodeId, data);
            } else if (topic.contains("/cmdReply/")) {
                handleCmdReplyData(nodeId, data);
            }
            
        } catch (Exception e) {
            log.error("处理 MQTT 消息失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 消息发送完成回调（用于发布消息时）
    }
    
    private String extractNodeId(String topic) {
        // topic 格式: iot/device/{subKey}/realTimeData/{nodeId}
        String[] parts = topic.split("/");
        if (parts.length >= 5) {
            return parts[parts.length - 1];
        }
        return null;
    }
    
    private void handleRealTimeData(String nodeId, Map<String, Object> data) {
        log.info("收到设备 {} 的实时数据", nodeId);
        deviceRealTimeData.put(nodeId, data);
        dataHandlerService.handleRealTimeData(nodeId, data);
    }
    
    private void handleStatusData(String nodeId, Map<String, Object> data) {
        log.info("收到设备 {} 的状态数据", nodeId);
        String status = (String) data.get("status");
        if (status != null) {
            deviceStatus.put(nodeId, status);
        }
        dataHandlerService.handleStatusData(nodeId, data);
    }
    
    private void handleLifeCycleData(String nodeId, Map<String, Object> data) {
        log.info("收到设备 {} 的生命周期数据", nodeId);
        dataHandlerService.handleLifeCycleData(nodeId, data);
    }
    
    private void handleCmdReplyData(String nodeId, Map<String, Object> data) {
        log.info("收到设备 {} 的命令回复", nodeId);
        dataHandlerService.handleCmdReplyData(nodeId, data);
    }
    
    // 获取设备的实时数据
    public Map<String, Object> getDeviceRealTimeData(String nodeId) {
        return deviceRealTimeData.get(nodeId);
    }
    
    // 获取所有设备的实时数据
    public Map<String, Map<String, Object>> getAllDeviceRealTimeData() {
        return new ConcurrentHashMap<>(deviceRealTimeData);
    }
    
    // 获取设备状态
    public String getDeviceStatus(String nodeId) {
        return deviceStatus.getOrDefault(nodeId, "unknown");
    }
    
    // 获取所有设备状态
    public Map<String, String> getAllDeviceStatus() {
        return new ConcurrentHashMap<>(deviceStatus);
    }
    
    // 发布命令到设备
    public void publishCommand(String nodeId, String command) {
        try {
            String topic = mqttConfig.getCmdReplyTopic(nodeId);
            MqttMessage message = new MqttMessage(command.getBytes(StandardCharsets.UTF_8));
            message.setQos(mqttConfig.getSubscription().getQos());
            mqttClient.publish(topic, message);
            log.info("已发布命令到设备 {}: {}", nodeId, command);
        } catch (MqttException e) {
            log.error("发布命令失败: {}", e.getMessage(), e);
        }
    }
    
    // 检查连接状态
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}
