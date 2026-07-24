package org.mjc;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = {"org.mjc.mapper"})
public class OnlineVoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineVoteApplication.class, args);
    }
}
