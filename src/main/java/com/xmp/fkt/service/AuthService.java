package com.xmp.fkt.service;

import org.springframework.http.ResponseEntity;

import com.xmp.fkt.requestDto.AuthRequest;
import com.xmp.fkt.requestDto.OtpModel;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.AuthResponse;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.util.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UsersResponse>> register(UsersRequest request);

	ResponseEntity<String> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse response);

	ResponseEntity<ResponseStructure<AuthResponse>> logout(String accessToken, String refreshToken, HttpServletRequest request, HttpServletResponse response);

}
