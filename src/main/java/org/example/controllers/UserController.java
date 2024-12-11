package org.example.controllers;

import org.example.DTO.UserDTO;
import org.example.Repositories.UserRepository;
import org.example.entites.User;
import org.example.services.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    KafkaProducer kafkaProducer;
    UserRepository userRepository;

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

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus());
        user.setBirthday(userDTO.getBirthday());

        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Validated @RequestBody UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findById(id);
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDTO.getName());
            user.setLastname(userDTO.getLastname());
            user.setEmail(userDTO.getEmail());
            user.setStatus(userDTO.getStatus());
            user.setBirthday(userDTO.getBirthday());

            User updatedUser = userRepository.save(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    public  UserController(@Autowired UserRepository userRepository, @Autowired KafkaProducer kafkaProducer)
    {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

}
