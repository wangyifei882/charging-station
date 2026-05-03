package com.charging.station.dto.iot;

public class IotTokenResponse {
    private Integer code;
    private String message;
    private TokenData data;
    
    public static class TokenData {
        private String accessToken;
        private Integer expiresIn;
        private String tokenType;
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public Integer getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    }
    
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public TokenData getData() { return data; }
    public void setData(TokenData data) { this.data = data; }
}
