package org.example.finala.service.custom.impl;


import lombok.RequiredArgsConstructor;
import org.example.finala.dto.AuthRequest;
import org.example.finala.entity.User;
import org.example.finala.exception.CustomException;
import org.example.finala.repository.UserRepository;
import org.example.finala.service.custom.AuthService;
import org.example.finala.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid password");
        }

        return jwtUtil.generateToken(user);
    }
}