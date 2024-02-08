package com.xmp.fkt.service;

import org.springframework.http.ResponseEntity;

import com.xmp.fkt.requestDto.OtpModel;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.util.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UsersResponse>> register(UsersRequest request);

	ResponseEntity<String> verifyOTP(OtpModel otpModel);

}
