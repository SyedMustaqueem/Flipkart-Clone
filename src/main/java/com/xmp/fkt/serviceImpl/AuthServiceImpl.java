package com.xmp.fkt.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xmp.fkt.entity.Customer;
import com.xmp.fkt.entity.Seller;
import com.xmp.fkt.entity.User;
import com.xmp.fkt.repository.CustomerRepo;
import com.xmp.fkt.repository.SellerRepo;
import com.xmp.fkt.repository.UserRepo;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.service.AuthService;
import com.xmp.fkt.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	PasswordEncoder passwordEncoder;
	private ResponseStructure<UsersResponse> structure;
	
	private UserRepo userRepo;
	
	private CustomerRepo customerRepo;

	private SellerRepo sellerRepo;

	@SuppressWarnings("unchecked")
	private  <T extends User>  T mapToUserRequest(UsersRequest request) {
		User user = null;
		switch (request.getUserRole()){
		case CUSTOMER ->{
			user  = new Customer();
		}
		case SELLER -> {
			user = new Seller();
		}
		}
		user.setEmail(request.getEmail());
		user.setUserName(request.getEmail().split("@")[0]);
		user.setPassword(passwordEncoder.encode(request.getPassword())); 
		user.setUserRole(request.getUserRole());
		return (T) user;
	}



	private UsersResponse mapToUserResponse(User users) {
		return UsersResponse.builder()
				.userId(users.getUserId())
				.userName(users.getUserName())
				.email(users.getEmail())
				.userRole(users.getUserRole())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> register(UsersRequest request) {
//		if(userRepo.existsByEmail(request.getEmail()))throw new RuntimeException();
//		User user = mapToUserRequest(request);
//		user=saveUser(user);
		//UsersResponse response = mapToUserResponse(user);
		User user=userRepo.findByUserName(request.getEmail().split("@")[0]).map(u->{
			if(u.isEmailverified()) throw new RuntimeException("user alredy exist with the specified"
					+ "email id  ");
			else {
			
				// send an e mail to client with OTP
				
			}
			return u;
		}).orElse(saveUser(mapToUserRequest(request)));
		
		return new ResponseEntity<ResponseStructure<UsersResponse>> 
		(structure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("Please Varify Your email Id using OTP sent")
				.setData(mapToUserResponse(user)),  HttpStatus.ACCEPTED);
	}


	private User saveUser(User user) {
		switch (user.getUserRole()){
		case CUSTOMER ->{
			user  = customerRepo.save((Customer)user);
		}
		case SELLER -> {
			user  = sellerRepo.save((Seller)user);
		}
		default ->throw new RuntimeException();
		}
		return user;
	}

}
