package com.charging.station.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    
    @Autowired
    private IotPlatformConfig iotConfig;
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(iotConfig.getHttp().getConnectTimeout());
        factory.setReadTimeout(iotConfig.getHttp().getReadTimeout());
        return new RestTemplate(factory);
    }
}
