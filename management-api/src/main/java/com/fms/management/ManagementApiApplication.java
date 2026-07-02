package com.fms.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fms.management.mapper")
public class ManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementApiApplication.class, args);
    }
}
