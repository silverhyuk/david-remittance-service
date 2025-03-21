package com.wirebarley.remittance.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.wirebarley.remittance")
@EntityScan(basePackages = "com.wirebarley.remittance.infrastructure")
@EnableJpaRepositories(basePackages = "com.wirebarley.remittance.infrastructure")
public class RemittanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RemittanceServiceApplication.class, args);
    }
}
