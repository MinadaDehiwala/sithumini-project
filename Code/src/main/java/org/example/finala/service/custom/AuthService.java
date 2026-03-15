package org.example.finala.service.custom;

import org.example.finala.dto.AuthRequest;
import org.example.finala.entity.User;
import org.example.finala.exception.CustomException;

public interface AuthService {
    public String login(AuthRequest request);
}
