package io.mattinfern0.kanbanboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class KanbanboardapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanboardapiApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
