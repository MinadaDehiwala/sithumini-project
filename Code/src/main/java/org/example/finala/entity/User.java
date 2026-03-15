package org.example.finala.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.finala.entity.Role;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // avoid reserved keywords
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;
}