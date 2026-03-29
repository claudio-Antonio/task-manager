package com.task_manager.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.enums.UserRole;
import com.task_manager.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .login("admin")
                .password("encoded_password")
                .email("admin@email.com")
                .role(UserRole.ADMIN)
                .build();
    }

    // -------------------------
    // loadUserByUsername
    // -------------------------

    @Test
    void loadUserByUsername_shouldReturnUserWhenFound() {
        when(userRepository.findByLogin("admin")).thenReturn(user);

        UserDetails result = userService.loadUserByUsername("admin");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        verify(userRepository, times(1)).findByLogin("admin");
    }

    @Test
    void loadUserByUsername_shouldReturnNullWhenUserNotFound() {
        when(userRepository.findByLogin("nonexistent")).thenReturn(null);

        UserDetails result = userService.loadUserByUsername("nonexistent");

        assertThat(result).isNull();
        verify(userRepository, times(1)).findByLogin("nonexistent");
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthoritiesForAdmin() {
        when(userRepository.findByLogin("admin")).thenReturn(user);

        UserDetails result = userService.loadUserByUsername("admin");

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthoritiesForManager() {
        User manager = User.builder()
                .id(2L)
                .login("manager")
                .password("encoded_password")
                .email("manager@email.com")
                .role(UserRole.MANAGER)
                .build();

        when(userRepository.findByLogin("manager")).thenReturn(manager);

        UserDetails result = userService.loadUserByUsername("manager");

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_MANAGER");
    }

    @Test
    @DisplayName("Should load by username")
    void loadUserByUsername_shouldReturnCorrectAuthoritiesForCollaborator() {
        User collaborator = User.builder()
                .id(3L)
                .login("collaborator")
                .password("encoded_password")
                .email("collaborator@email.com")
                .role(UserRole.COLLABORATOR)
                .build();

        when(userRepository.findByLogin("collaborator")).thenReturn(collaborator);

        UserDetails result = userService.loadUserByUsername("collaborator");

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }
}