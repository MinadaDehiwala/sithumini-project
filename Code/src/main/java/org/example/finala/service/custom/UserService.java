package org.example.finala.service.custom;

import org.example.finala.dto.UserDTO;
import org.example.finala.entity.Role;
import org.example.finala.entity.User;
import org.example.finala.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

public interface UserService {
    public UserDTO register(UserDTO dto) ;

    public UserDTO getProfile(String email);

    public UserDTO updateProfile(String email, UserDTO dto) ;

    public void deleteProfile(String email);

    public List<UserDTO> getAllUsers() ;
}
