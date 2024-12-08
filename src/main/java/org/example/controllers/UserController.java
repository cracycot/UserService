package org.example.controllers;

import org.example.services.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    KafkaProducer kafkaProducer;

    @Autowired
    private void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<?> createPost(@RequestBody String message) {
        try {
            kafkaProducer.sendMessage(message);
            return ResponseEntity.ok().body("Сообщение отправлено");
        } catch (Exception e) {
//            log.error("Ошибка при получении владельца", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка");
        }
    }

}
