package org.example.finala.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.finala.dto.UserDTO;
import org.example.finala.service.custom.impl.UserServiceImpl;
import org.example.finala.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<List<UserDTO>>> getAllUsers() {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Users fetched successfully", userService.getAllUsers()),
                HttpStatus.OK
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserDTO>> getProfile(Principal principal) {
        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile fetched successfully", userService.getProfile(principal.getName())),
                HttpStatus.OK
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<APIResponse<UserDTO>> updateProfile(
            Principal principal,
            @Valid @RequestBody UserDTO dto) {

        return new ResponseEntity<>(
                new APIResponse<>(200, "Profile updated successfully", userService.updateProfile(principal.getName(), dto)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/profile")
    public ResponseEntity<APIResponse<String>> deleteProfile(Principal principal) {
        userService.deleteProfile(principal.getName());
        return new ResponseEntity<>(
                new APIResponse<>(200, "Account deleted successfully", null),
                HttpStatus.OK
        );
    }
}