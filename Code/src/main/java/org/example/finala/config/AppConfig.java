package org.example.finala.config;

import lombok.RequiredArgsConstructor;
import org.example.finala.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {

        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        ))
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}