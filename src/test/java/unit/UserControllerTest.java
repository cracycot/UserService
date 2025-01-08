package unit;

import org.example.DTO.UserDTO;
import org.example.controllers.UserController;
import org.example.entites.User;
import org.example.repositories.UserRepository;
import org.example.services.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UserController mockUserController;

    @Test
    void createPostSuccess() {
        Mockito.doNothing().when(kafkaProducer).sendMessage(Mockito.anyString());
        ResponseEntity<?> response = mockUserController.createPost("Test message");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(kafkaProducer, Mockito.times(1)).sendMessage("Test message");
    }

    @Test
    void createPostFailure() {
        Mockito.doThrow(new RuntimeException("Kafka error")).when(kafkaProducer).sendMessage(Mockito.anyString());
        ResponseEntity<?> response = mockUserController.createPost("Test message");
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Mockito.verify(kafkaProducer, Mockito.times(1)).sendMessage("Test message");
    }

    @Test
    void createUserSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setBirthday(new Date(1, 1, 1));
        userDTO.setEmail("test@gmail.com");
        userDTO.setName("test");
        userDTO.setLastname("test");
        userDTO.setStatus("active");

        User savedUser = new User();
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        ResponseEntity<User> response = mockUserController.createUser(userDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void getAllUsersSuccess() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        Mockito.when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = mockUserController.getAllUsers();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void updateUserSuccess() {
        User existingUser = new User();
        existingUser.setName("oldName");

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("newName");
        userDTO.setLastname("newLastname");
        userDTO.setEmail("newEmail@test.com");
        userDTO.setStatus("active");
        userDTO.setBirthday(new Date(1, 1, 1));

        ResponseEntity<User> response = mockUserController.updateUser(1L, userDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("newName", response.getBody().getName());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        UserDTO userDTO = new UserDTO();
        ResponseEntity<User> response = mockUserController.updateUser(1L, userDTO);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    void deleteUserSuccess() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(userRepository).deleteById(Mockito.anyLong());

        ResponseEntity<Void> response = mockUserController.deleteUser(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteUserNotFound() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        ResponseEntity<Void> response = mockUserController.deleteUser(1L);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        Mockito.verify(userRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void getUserByIdSuccess() {
        User user = new User();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        ResponseEntity<User> response = mockUserController.getUserById(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void getUserByIdNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        ResponseEntity<User> response = mockUserController.getUserById(1L);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }
}