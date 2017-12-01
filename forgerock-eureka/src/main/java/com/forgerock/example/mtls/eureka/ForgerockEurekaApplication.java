package com.forgerock.example.mtls.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ForgerockEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForgerockEurekaApplication.class, args);
    }

}
