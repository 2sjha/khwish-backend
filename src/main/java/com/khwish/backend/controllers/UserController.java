package com.khwish.backend.controllers;

import com.khwish.backend.requests.users.UserNotificationTokenRequest;
import com.khwish.backend.requests.users.UserLoginOAuthRequest;
import com.khwish.backend.requests.users.UserLogoutRequest;
import com.khwish.backend.requests.users.UserOnBoardingRequest;
import com.khwish.backend.requests.users.WithdrawalRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    private BaseResponse login(@RequestBody UserLoginOAuthRequest userLoginRequest, HttpServletResponse response) {
        BaseResponse result = userService.login(userLoginRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/on-board")
    private BaseResponse login(@RequestBody UserOnBoardingRequest userOnBoardingRequest, HttpServletResponse response) {
        BaseResponse result = userService.onBoardUser(userOnBoardingRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/notification-token")
    private BaseResponse notificationToken(@RequestAttribute("user-id") UUID userId, @RequestBody UserNotificationTokenRequest userNotificationTokenRequest, HttpServletResponse response) {
        BaseResponse result = userService.saveUserNotificationToken(userId, userNotificationTokenRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @GetMapping("/home")
    private BaseResponse homePage(@RequestAttribute("user-id") UUID userId, HttpServletResponse response) {
        BaseResponse result = userService.getHomePage(userId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @GetMapping("/profile")
    private BaseResponse profile(@RequestAttribute("user-id") UUID userId, HttpServletResponse response) {
        BaseResponse result = userService.getProfile(userId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/withdraw")
    private BaseResponse withdraw(@RequestAttribute("user-id") UUID userId, @RequestBody WithdrawalRequest request, HttpServletResponse response) {
        BaseResponse result = userService.withdraw(userId, request);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @GetMapping("/wallet-activities")
    private BaseResponse walletActivities(@RequestAttribute("user-id") UUID userId, HttpServletResponse response) {
        BaseResponse result = userService.getWalletActivities(userId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/logout")
    private BaseResponse logout(@RequestAttribute("user-id") UUID userId, @RequestBody UserLogoutRequest userLogoutRequest, HttpServletResponse response) {
        BaseResponse result = userService.logout(userLogoutRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }
}