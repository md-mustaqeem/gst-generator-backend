package com.gst.service;

import com.gst.requestDto.*;
import com.gst.responseDto.AuthResponse;
import com.gst.responseDto.UserResponse;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    public String sendOtp(ForgotPasswordRequest request);

    public String verifyOtp(VerifyOtpRequest request);

    public String resetPassword(ResetPasswordRequest request);


    String sendEmailLoginOtp(LoginOtpRequest request);

    AuthResponse verifyEmailLoginOtp(VerifyLoginOtpRequest request);

    String sendMobileLoginOtp(LoginOtpRequest request);

    AuthResponse verifyMobileLoginOtp(VerifyLoginOtpRequest request);


    // ==========================
    // User Profile
    // ==========================

    UserResponse getProfile();

    UserResponse updateProfile(RegisterRequest request);

}
