package com.task_manager.demo.repositories;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = User.builder()
                .login("manager")
                .password("encoded_password")
                .email("manager@email.com")
                .role(UserRole.MANAGER)
                .build();

        userRepository.save(user);
    }

    @Test
    void findByLogin_shouldReturnUserWhenExists() {
        UserDetails result = userRepository.findByLogin("manager");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("manager");
    }

    @Test
    void findByLogin_shouldReturnNullWhenNotExists() {
        UserDetails result = userRepository.findByLogin("nonexistent");

        assertThat(result).isNull();
    }

    @Test
    void save_shouldPersistUser() {
        User newUser = User.builder()
                .login("admin")
                .password("encoded_password")
                .email("admin@email.com")
                .role(UserRole.ADMIN)
                .build();

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLogin()).isEqualTo("admin");
        assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        Optional<User> result = userRepository.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("manager");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_shouldRemoveUser() {
        userRepository.delete(user);

        Optional<User> result = userRepository.findById(user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User anotherUser = User.builder()
                .login("collaborator")
                .password("encoded_password")
                .email("collaborator@email.com")
                .role(UserRole.COLLABORATOR)
                .build();
        userRepository.save(anotherUser);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
    }
}