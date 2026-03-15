package org.example.finala.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.finala.dto.AuthRequest;
import org.example.finala.dto.AuthResponse;
import org.example.finala.dto.UserDTO;
import org.example.finala.util.APIResponse;
import org.example.finala.service.custom.impl.AuthServiceImpl;
import org.example.finala.service.custom.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserDTO>> register(@Valid @RequestBody UserDTO dto) {

        UserDTO savedUser = userService.register(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        201,
                        "User registered successfully",
                        savedUser
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {

        String token = authService.login(request);

        AuthResponse response = new AuthResponse(token);

        return new ResponseEntity<>(
                new APIResponse<>(
                        200,
                        "Login successful",
                        response
                ),
                HttpStatus.OK
        );
    }
}