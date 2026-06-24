package com.gst.controller;

import com.gst.requestDto.*;
import com.gst.responseDto.AuthResponse;
import com.gst.responseDto.UserResponse;
import com.gst.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid RegisterRequest request) {

        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request){

        return ResponseEntity.ok(userService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody VerifyOtpRequest request){

        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request){

        return ResponseEntity.ok(userService.resetPassword(request));
    }

    // Direct Mobile and Email Login

    @PostMapping("/login/email/send-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody LoginOtpRequest request) {

        return ResponseEntity.ok(userService.sendEmailLoginOtp(request));
    }

    @PostMapping("/login/email/verify-otp")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody VerifyLoginOtpRequest request) {

        return ResponseEntity.ok(userService.verifyEmailLoginOtp(request));
    }

    @PostMapping("/login/mobile/send-otp")
    public ResponseEntity<?> sendMobileOtp(@RequestBody LoginOtpRequest request) {

        return ResponseEntity.ok(userService.sendMobileLoginOtp(request));
    }

    @PostMapping("/login/mobile/verify-otp")
    public ResponseEntity<?> verifyMobileOtp(@RequestBody VerifyLoginOtpRequest request) {

        return ResponseEntity.ok(userService.verifyMobileLoginOtp(request));
    }


    // Get Logged-in User Profile
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    // Update Logged-in User Profile
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(userService.updateProfile(request));
    }


}