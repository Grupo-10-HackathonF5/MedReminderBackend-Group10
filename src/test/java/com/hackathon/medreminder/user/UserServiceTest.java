package com.hackathon.medreminder.user;

import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.exception.UserAlreadyExistsByEmail;
import com.hackathon.medreminder.user.exception.UserNotFoundByUsername;
import com.hackathon.medreminder.user.repository.UserRepository;
import com.hackathon.medreminder.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testUser");
        user.setPassword("password123");
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(user);


        assertNotEquals("password123", createdUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("password123", createdUser.getPassword()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_throwsUserAlreadyExistsByEmail() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        UserAlreadyExistsByEmail exception = assertThrows(
                UserAlreadyExistsByEmail.class,
                () -> userService.createUser(user)
        );

        assertTrue(exception.getMessage().contains(user.getEmail()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByUsername_found() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername(user.getUsername());

        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }

    @Test
    void getUserByUsername_throwsUserNotFoundByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByUsername.class,
                () -> userService.getUserByUsername(user.getUsername()));
    }
}

