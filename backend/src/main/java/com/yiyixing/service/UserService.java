package com.yiyixing.service;

import com.yiyixing.dto.request.LoginRequest;
import com.yiyixing.dto.request.RegisterRequest;
import com.yiyixing.dto.response.AuthResponse;
import com.yiyixing.dto.response.UserResponse;
import com.yiyixing.entity.User;

public interface UserService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    UserResponse getCurrentUser(Long userId);

    User getUserById(Long userId);
}
