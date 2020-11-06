package com.senior.prevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PreventSeniorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreventSeniorApplication.class, args);
    }

}
