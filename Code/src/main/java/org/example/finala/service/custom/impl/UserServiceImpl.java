package org.example.finala.service.custom.impl;

import lombok.RequiredArgsConstructor;
import org.example.finala.dto.UserDTO;
import org.example.finala.entity.Role;
import org.example.finala.entity.User;
import org.example.finala.exception.CustomException;
import org.example.finala.repository.UserRepository;
import org.example.finala.service.custom.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException("Email already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateProfile(String email, UserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        if(dto.getUsername() != null) user.setUsername(dto.getUsername());
        if(dto.getEmail() != null) user.setEmail(dto.getEmail());
        if(dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(u -> modelMapper.map(u, UserDTO.class)).collect(Collectors.toList());
    }
}