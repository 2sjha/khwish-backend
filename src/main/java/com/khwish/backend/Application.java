package com.khwish.backend;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {

        BasicConfigurator.configure();
        SpringApplication.run(Application.class, args);
    }
}