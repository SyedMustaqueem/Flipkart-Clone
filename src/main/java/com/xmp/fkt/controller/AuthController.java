package com.xmp.fkt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.service.AuthService;
import com.xmp.fkt.util.ResponseStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
//@RequestMapping("/api/v1")
@AllArgsConstructor//insted of using auto wire we will user all arg  constructor cz it will provide mock some issues while writting the test cases.
public class AuthController {
	

	private AuthService authService;	
	//autowore always does field injution.
	//alwsys recomended to use constructor 
	@PostMapping("/users/user")
	public ResponseEntity<ResponseStructure<UsersResponse>> register(@RequestBody @Valid UsersRequest request){
	return authService.register(request);
	}
	
}
