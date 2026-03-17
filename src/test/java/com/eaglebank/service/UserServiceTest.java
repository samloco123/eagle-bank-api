package com.eaglebank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eaglebank.dto.CreateUserRequest;
import com.eaglebank.exception.ConflictException;
import com.eaglebank.model.Address;
import com.eaglebank.model.User;
import com.eaglebank.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_Success() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPhoneNumber("+441234567890");
        request.setPassword("password123");

        Address address = new Address();
        address.setLine1("123 Test St");
        address.setTown("Test Town");
        address.setCounty("Test County");
        address.setPostcode("TE1 1ST");
        request.setAddress(address);

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("usr-abc123");
            return user;
        });

        org.mockito.Mockito.doNothing().when(entityManager).flush();

        // Act
        var result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("usr-abc123", result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any());
        verify(entityManager, times(1)).flush();
    }

    @Test
    void createUser_DuplicateEmail_ThrowsConflictException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@example.com");
        when(userRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }
}
