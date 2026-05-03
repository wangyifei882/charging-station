package com.charging.station;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.charging.station.mapper")
public class ChargingStationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargingStationApplication.class, args);
        System.out.println("========================================");
        System.out.println("  充电桩运营管理平台启动成功!");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
