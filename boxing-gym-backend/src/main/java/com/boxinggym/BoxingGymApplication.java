package com.boxinggym;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 拳馆管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.boxinggym.mapper")
@EnableScheduling
public class BoxingGymApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxingGymApplication.class, args);
    }
}
