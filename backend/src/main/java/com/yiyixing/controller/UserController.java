package com.yiyixing.controller;

import com.yiyixing.dto.response.ApiResponse;
import com.yiyixing.dto.response.UserResponse;
import com.yiyixing.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口 — 需要认证。通过请求属性获取当前用户 ID。
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前登录用户信息。
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(
            @org.springframework.web.bind.annotation.RequestAttribute("userId") Long userId) {
        return ApiResponse.success(userService.getCurrentUser(userId));
    }
}
