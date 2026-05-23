package com.tictactoe.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UiBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UiBackendApplication.class, args);
    }
}
