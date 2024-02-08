package com.xmp.fkt.serviceImpl;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xmp.fkt.cache.CacheStore;
import com.xmp.fkt.entity.Customer;
import com.xmp.fkt.entity.Seller;
import com.xmp.fkt.entity.User;
import com.xmp.fkt.repository.CustomerRepo;
import com.xmp.fkt.repository.SellerRepo;
import com.xmp.fkt.repository.UserRepo;
import com.xmp.fkt.requestDto.OtpModel;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.service.AuthService;
import com.xmp.fkt.util.MessageStructure;
import com.xmp.fkt.util.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private JavaMailSender mailSender;

	private PasswordEncoder passwordEncoder;

	private ResponseStructure<UsersResponse> structure;

	private UserRepo userRepo;

	private CustomerRepo customerRepo;

	private SellerRepo sellerRepo;

	private CacheStore<String> otpCacheStore;

	private CacheStore<User> userCacheStore; 

	@SuppressWarnings("unchecked")
	private <T extends User> T mapToUserRequest(UsersRequest request) {
		User user = null;
		switch (request.getUserRole()) {
		case CUSTOMER -> {
			user = new Customer();
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
		return UsersResponse.builder().userId(users.getUserId()).userName(users.getUserName()).email(users.getEmail())
				.userRole(users.getUserRole()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> register(UsersRequest request) {
		if (userRepo.existsByEmail(request.getEmail()))
			throw new RuntimeException("User does not exist by this email id");
		String OTP = generateOTP();//"45789";
		User user = mapToUserRequest(request);
		userCacheStore.add(request.getEmail(), user);
		otpCacheStore.add(request.getEmail(), OTP);

		try {
			sendOtpToMail(user, OTP);
		} catch (MessagingException e) {
			log.error("The email address doesnt exist"+OTP);
			e.printStackTrace();
		} 
		return new ResponseEntity<ResponseStructure<UsersResponse>>(
				structure.setStatus(HttpStatus.ACCEPTED.value())
				//.setMessage("Through OTP :" +OTP)
				.setMessage("Please check the mail to u might have reacived OTP")
				.setData(mapToUserResponse(user)),
				HttpStatus.ACCEPTED);
	}

	private void sendOtpToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Complete Your Registration to flipkart")
				.sendDate(new Date())
				.text( 
						"hey," +user.getUserName()
						+ "good to see you are intrested in flipkart"
						+ "complete your registration using the otp <br>"
						+ "<h1>"+otp+"</h1><br>"
						+ " Note : the OTP expires in 1 minuite"
						+ "<br><br>"
						+ "with best regards<br>"
						+ "flipkart"

						).build());
	}

//	private void sendWelcomeMail(User user) throws MessagingException {
//		sendMail(MessageStructure.builder()
//				.to(user.getEmail())
//				.subject("Welcoe to Flipkart")
//				.sendDate(new Date())
//				.text("hey," +user.getUserName()
//				+ "Welcome To Flopkart"
//				+ "We are happy To be a part of your Life"
//				+ "<br><br>"
//				+ "with best regards<br>"
//				+ "flipkart"
//						).build());
//	}

	@Async//amking method a syncromous by annoting as async
	private void sendMail(MessageStructure message) throws MessagingException {
		MimeMessage mineMessage= mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mineMessage,true);
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSendDate());
		helper.setText(message.getText(),true);
		mailSender.send(mineMessage);
	}

	private String generateOTP() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public ResponseEntity<String> verifyOTP(OtpModel otpModel) {
		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());
		if(otp==null) throw new RuntimeException("OTP expired");
		if(user==null) throw new RuntimeException("Registartion Session Expired");
		if(!otp.equals(otpModel.getOtp())) throw new RuntimeException("Invalied Exception");
		user.setEmailverified(true);
		userRepo.save(user);
//		try {
//			sendWelcomeMail(user);
//		} catch (MessagingException e) {
//			log.error("Could not send the welcome request");
//			e.printStackTrace();
//		}
		return new ResponseEntity<String>("registrarion Sucessfull",HttpStatus.CREATED);
		
	}

	private User saveUser(User user) {
		switch (user.getUserRole()) {
		case CUSTOMER -> {
			user = customerRepo.save((Customer) user);
		}
		case SELLER -> {
			user = sellerRepo.save((Seller) user);
		}
		default -> throw new RuntimeException();
		}
		return user;
	}

}
