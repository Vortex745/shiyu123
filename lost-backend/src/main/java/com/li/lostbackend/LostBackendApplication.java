package com.li.lostbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import xyz.erupt.core.annotation.EruptScan;

@EntityScan // 开启JPA实体扫描
@EruptScan  // 开启Erupt扫描
@EnableAsync
@SpringBootApplication
public class LostBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostBackendApplication.class, args);
    }

}
