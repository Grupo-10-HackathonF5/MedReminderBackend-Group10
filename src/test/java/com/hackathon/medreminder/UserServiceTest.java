package com.hackathon.medreminder;

import com.hackathon.medreminder.user.dto.UserMapper;
import com.hackathon.medreminder.user.dto.UserRequestDTO;
import com.hackathon.medreminder.user.dto.UserResponseDTO;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.repository.UserRepository;
import com.hackathon.medreminder.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = UserRequestDTO.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("encodedPass")
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(requestDTO));
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }
}
