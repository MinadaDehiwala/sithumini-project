package com.aurabloom;

import com.aurabloom.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class AuraBloomApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuraBloomApplication.class, args);
    }
}
