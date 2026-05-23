package com.oidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class OidcApplication {
    public static void main(String[] args) {
        SpringApplication.run(OidcApplication.class, args);
    }
}
