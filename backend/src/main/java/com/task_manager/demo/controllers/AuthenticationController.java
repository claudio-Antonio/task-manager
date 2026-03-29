package com.task_manager.demo.controllers;

import com.task_manager.demo.domain.User;
import com.task_manager.demo.dtos.AuthenticationDTO;
import com.task_manager.demo.dtos.LoginResponseDTO;
import com.task_manager.demo.dtos.RegisterDTO;
import com.task_manager.demo.infra.security.SecurityConfigurations;
import com.task_manager.demo.repositories.UserRepository;
import com.task_manager.demo.infra.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "controller to register and authenticate new users")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "User login")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @ApiResponse(responseCode = "404", description = "BadRequestException, user not authenticated")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok().body(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @Operation(summary = "save new users")
    @ApiResponse(responseCode = "201", description = "New user registered")
    @ApiResponse(responseCode = "400", description = "BadRequestException, user exists")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        System.out.println("REGISTER DATA: " + data);
        if(userRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String password = new BCryptPasswordEncoder().encode(data.password());
        User user = User.builder()
                .login(data.login())
                .role(data.role())
                .password(password)
                .email(data.email())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
