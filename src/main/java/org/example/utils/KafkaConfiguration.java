package org.example.utils;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic newTopic() {
        return new NewTopic(
                "posts",
                2,
                (short) 1
        );
    }
}
