package io.ssstoyanov.mooch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MoochBot {
    public static void main(String[] args) {
        SpringApplication.run(MoochBot.class, args);
    }
}