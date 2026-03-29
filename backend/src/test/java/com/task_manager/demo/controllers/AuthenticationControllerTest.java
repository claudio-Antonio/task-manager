package com.task_manager.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.dtos.AuthenticationDTO;
import com.task_manager.demo.dtos.LoginResponseDTO;
import com.task_manager.demo.dtos.RegisterDTO;
import com.task_manager.demo.enums.UserRole;
import com.task_manager.demo.repositories.UserRepository;
import com.task_manager.demo.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .login("manager")
                .password("encoded_password")
                .email("manager@email.com")
                .role(UserRole.MANAGER)
                .build();
    }

    // -------------------------
    // POST /auth/login
    // -------------------------

    @Test
    void login_shouldReturn200WithTokenWhenCredentialsAreValid() {
        AuthenticationDTO loginRequest = new AuthenticationDTO("manager", "password123");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("mocked-jwt-token");

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo("mocked-jwt-token");
        verify(tokenService, times(1)).generateToken(user);
    }

    @Test
    void login_shouldThrowWhenCredentialsAreInvalid() {
        AuthenticationDTO loginRequest = new AuthenticationDTO("manager", "wrong_password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationController.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void login_shouldCallAuthenticationManagerWithCorrectCredentials() {
        AuthenticationDTO loginRequest = new AuthenticationDTO("manager", "password123");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("mocked-jwt-token");

        authenticationController.login(loginRequest);

        verify(authenticationManager, times(1)).authenticate(
                argThat(token -> token.getName().equals("manager"))
        );
    }

    // -------------------------
    // POST /auth/register
    // -------------------------

    @Test
    void register_shouldReturn200WhenUserIsCreatedSuccessfully() {
        RegisterDTO registerRequest = new RegisterDTO("newuser", "newuser@email.com", "password123", UserRole.COLLABORATOR);

        when(userRepository.findByLogin("newuser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Void> response = authenticationController.register(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldReturn400WhenLoginAlreadyExists() {
        RegisterDTO registerRequest = new RegisterDTO("manager", "manager@email.com", "password123", UserRole.MANAGER);

        when(userRepository.findByLogin("manager")).thenReturn(user);

        ResponseEntity<Void> response = authenticationController.register(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldEncodePasswordBeforeSaving() {
        RegisterDTO registerRequest = new RegisterDTO("newuser", "newuser@email.com", "plaintext_password", UserRole.COLLABORATOR);

        when(userRepository.findByLogin("newuser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticationController.register(registerRequest);

        verify(userRepository).save(argThat(savedUser ->
                !savedUser.getPassword().equals("plaintext_password")
        ));
    }

    @Test
    void register_shouldSaveUserWithCorrectRole() {
        RegisterDTO registerRequest = new RegisterDTO("adminuser", "admin@email.com", "password123", UserRole.ADMIN);

        when(userRepository.findByLogin("adminuser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticationController.register(registerRequest);

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getRole() == UserRole.ADMIN
        ));
    }
}