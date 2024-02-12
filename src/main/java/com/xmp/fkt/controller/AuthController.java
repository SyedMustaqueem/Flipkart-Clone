package com.xmp.fkt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xmp.fkt.requestDto.AuthRequest;
import com.xmp.fkt.requestDto.OtpModel;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.AuthResponse;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.service.AuthService;
import com.xmp.fkt.util.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;

@RestController
//@RequestMapping("/api/v1")
@AllArgsConstructor//insted of using auto wire we will user all arg  constructor cz it will provide mock some issues while writting the test cases.
public class AuthController {
	

	private AuthService authService;	
	//autowore always does field injution.
	//alwsys recomended to use constructor 
	@PostMapping("/user")
	public ResponseEntity<ResponseStructure<UsersResponse>> register(@RequestBody @Valid UsersRequest request){
	return authService.register(request);
	}
	
	@PostMapping("/verify-otp")
	public /*ResponseEntity<ResponseStructure<UsersResponse>>*/ ResponseEntity<String> verifyOTP(@RequestBody OtpModel otpModel) {
		return authService.verifyOTP(otpModel);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest, HttpServletResponse response){
		return authService.login(authRequest,response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(@CookieValue(name = "at", required = false) String accessToken , @CookieValue(name = "rt", required = false) String refreshToken ,HttpServletRequest request,HttpServletResponse response){
		return authService.logout(accessToken,refreshToken,request,response);
	}
	
}
