package com.gst.service;

import com.gst.entity.OtpToken;
import com.gst.entity.User;
import com.gst.repository.OtpRepository;
import com.gst.repository.UserRepository;
import com.gst.requestDto.*;
import com.gst.responseDto.AuthResponse;
import com.gst.responseDto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpRepository otpRepository;

    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByMobile(request.getMobile())) {
            throw new RuntimeException("Mobile number already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


    @Override
    public AuthResponse login(LoginRequest request) {

        User user;

        String username = request.getEmail(); // Email ya Mobile dono aa sakte hain

        // Agar sirf digits hain to mobile samjho
        if (username.matches("\\d+")) {

            user = userRepository.findByMobile(username)
                    .orElseThrow(() -> new RuntimeException("Invalid mobile or password"));

        } else {

            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


    public String sendOtp(ForgotPasswordRequest request){

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000));

        OtpToken token = new OtpToken();

        token.setOtp(otp);
        token.setVerified(false);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        if(request.getEmail()!=null && !request.getEmail().isBlank()){

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email not found"));





            token.setEmail(user.getEmail());

             emailService.sendOtp(user.getEmail(),otp);

        }else{

            User user = userRepository.findByMobile(request.getMobile())
                    .orElseThrow(()->new RuntimeException("Mobile not found"));

            token.setMobile(user.getMobile());

        }

        otpRepository.save(token);

        return "OTP Sent Successfully";

    }

    @Override
    public String verifyOtp(VerifyOtpRequest request) {

        OtpToken token;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            token = otpRepository.findTopByEmailOrderByIdDesc(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("OTP not found"));

        } else if (request.getMobile() != null && !request.getMobile().isBlank()) {

            token = otpRepository.findTopByMobileOrderByIdDesc(request.getMobile())
                    .orElseThrow(() -> new RuntimeException("OTP not found"));

        } else {
            throw new RuntimeException("Email or Mobile is required");
        }

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        if (!token.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        token.setVerified(true);
        otpRepository.save(token);

        return "OTP Verified Successfully";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {

        User user;
        OtpToken token;

        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            token = otpRepository.findTopByEmailOrderByIdDesc(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("OTP not found"));

        } else if (request.getMobile() != null && !request.getMobile().isBlank()) {

            user = userRepository.findByMobile(request.getMobile())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            token = otpRepository.findTopByMobileOrderByIdDesc(request.getMobile())
                    .orElseThrow(() -> new RuntimeException("OTP not found"));

        } else {
            throw new RuntimeException("Email or Mobile is required");
        }

        if (!token.isVerified()) {
            throw new RuntimeException("Please verify OTP first");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "Password Changed Successfully";
    }


    //Direct mobile  and Email Login

    @Override
    public String sendEmailLoginOtp(LoginOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpToken token = new OtpToken();
        token.setEmail(user.getEmail());
        token.setOtp(otp);
        token.setVerified(false);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(token);

        System.out.println("==================================");
        System.out.println("Email Login OTP : " + otp);
        System.out.println("==================================");
        emailService.sendOtp(user.getEmail(), otp);

        return "OTP Sent Successfully";
    }



    @Override
    public AuthResponse verifyEmailLoginOtp(VerifyLoginOtpRequest request) {

        OtpToken token = otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        if (!token.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


    @Override
    public String sendMobileLoginOtp(LoginOtpRequest request) {

        User user = userRepository.findByMobile(request.getMobile())
                .orElseThrow(() -> new RuntimeException("Mobile not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpToken token = new OtpToken();
        token.setMobile(user.getMobile());
        token.setOtp(otp);
        token.setVerified(false);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(token);

        System.out.println("==================================");
        System.out.println("Mobile Login OTP : " + otp);
        System.out.println("==================================");

        return "OTP Sent Successfully";
    }


    @Override
    public AuthResponse verifyMobileLoginOtp(VerifyLoginOtpRequest request) {

        OtpToken token = otpRepository
                .findTopByMobileOrderByIdDesc(request.getMobile())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        if (!token.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByMobile(request.getMobile())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


    @Override
    public UserResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .build();
    }

    @Override
    public UserResponse updateProfile(RegisterRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .build();
    }



}