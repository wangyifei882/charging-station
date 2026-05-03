package com.charging.station.dto.iot;

public class IotTokenRequest {
    private String appId;
    private String appSecret;
    private String grantType = "client_credentials";
    
    public IotTokenRequest() {}
    
    public IotTokenRequest(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }
    
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }
}
