package com.xmp.fkt.serviceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xmp.fkt.cache.CacheStore;
import com.xmp.fkt.entity.AccessToken;
import com.xmp.fkt.entity.Customer;
import com.xmp.fkt.entity.RefreshToken;
import com.xmp.fkt.entity.Seller;
import com.xmp.fkt.entity.User;
import com.xmp.fkt.exception.UserNotLoggedInException;
import com.xmp.fkt.repository.AccessTokenRepo;
import com.xmp.fkt.repository.CustomerRepo;
import com.xmp.fkt.repository.RefreshTokenRepo;
import com.xmp.fkt.repository.SellerRepo;
import com.xmp.fkt.repository.UserRepo;
import com.xmp.fkt.requestDto.AuthRequest;
import com.xmp.fkt.requestDto.OtpModel;
import com.xmp.fkt.requestDto.UsersRequest;
import com.xmp.fkt.responseDto.AuthResponse;
import com.xmp.fkt.responseDto.UsersResponse;
import com.xmp.fkt.security.JwtService;
import com.xmp.fkt.service.AuthService;
import com.xmp.fkt.util.CookieManager;
import com.xmp.fkt.util.MessageStructure;
import com.xmp.fkt.util.ResponseStructure;
import com.xmp.fkt.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private static final String value = null;

	private JavaMailSender mailSender;

	private PasswordEncoder passwordEncoder;

	private ResponseStructure<UsersResponse> structure;

	private UserRepo userRepo;

	private CustomerRepo customerRepo;

	private SellerRepo sellerRepo;

	private CacheStore<String> otpCacheStore;

	private CacheStore<User> userCacheStore; 

	private AuthenticationManager authenticationManager;

	private CookieManager cookieManager;

	private JwtService jwtService;

	private ResponseStructure<AuthResponse> authStructure;

	private AccessTokenRepo accessTokenRepo;

	private RefreshTokenRepo refreshTokenRepo;

	private SimpleResponseStructure simpleResponseStructure;
	
	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSeconds;



	/**
	 * @param mailSender
	 * @param passwordEncoder
	 * @param structure
	 * @param userRepo
	 * @param customerRepo
	 * @param sellerRepo
	 * @param otpCacheStore
	 * @param userCacheStore
	 * @param authenticationManager
	 * @param cookieManager
	 * @param jwtService
	 * @param authStructure
	 * @param accessTokenRepo
	 * @param refreshTokenRepo
	 * @param accessExpiryInSeconds
	 * @param refreshExpiryInSeconds
	 */
	public AuthServiceImpl(JavaMailSender mailSender,
			PasswordEncoder passwordEncoder,
			ResponseStructure<UsersResponse> structure, 
			UserRepo userRepo, CustomerRepo customerRepo,
			SellerRepo sellerRepo, 
			CacheStore<String> otpCacheStore, 
			CacheStore<User> userCacheStore,
			AuthenticationManager authenticationManager, 
			CookieManager cookieManager, 
			JwtService jwtService,
			ResponseStructure<AuthResponse> authStructure, 
			AccessTokenRepo accessTokenRepo,
			RefreshTokenRepo refreshTokenRepo) {
		super();
		this.mailSender = mailSender;
		this.passwordEncoder = passwordEncoder;
		this.structure = structure;
		this.userRepo = userRepo;
		this.customerRepo = customerRepo;
		this.sellerRepo = sellerRepo;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.authStructure = authStructure;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
	}

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

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse response) {
		String userName=authRequest.getEmail().split("@")[0];
		//new user name password authenetication token which takes the user anem and the user request.get passwoed
		UsernamePasswordAuthenticationToken token =new UsernamePasswordAuthenticationToken
				(userName,authRequest.getPassword());
		Authentication authenticate = authenticationManager.authenticate(token);
		if(!authenticate.isAuthenticated())
			throw new UsernameNotFoundException("Failed To Authenticate the user");
		else
		{
			//generating the cookie and authresponse and returning to the client.
			return userRepo.findByUserName(userName).map(user ->{

				grantAccess(response, user);
				return 	ResponseEntity.ok(authStructure.setStatus(HttpStatus.OK.value())
						.setData(AuthResponse.builder()
								.userId(user.getUserId())
								.userName(userName)
								.role(user.getUserName())
								.isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
								.refreshExpriration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
								.build()));
			}).get();


		}
	}


	@Override
	public ResponseEntity<ResponseStructure<SimpleResponseStructure>> revokeAllDeviceAcess(String accessToken,String refreshToken,HttpServletResponse response) {

		userRepo.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).
		ifPresent(user->{
			blockAccessToken(accessTokenRepo.findAllByUserAndIsBlocked(user,false));
			blockRefreshToken(refreshTokenRepo.findAllByUserAndIsBlocked(user,false));
		});
		
		simpleResponseStructure.setMessage("revoked All Device Acess Sucessfully");
		simpleResponseStructure.setStatus(HttpStatus.ACCEPTED.value());
		return new ResponseEntity<ResponseStructure<SimpleResponseStructure>> (HttpStatus.OK);
	}
	

	@Override
	public ResponseEntity<ResponseStructure<SimpleResponseStructure>> revokeOtherDeviceAcess(String accessToken,
			String refreshToken, HttpServletResponse response,String token) {
		userRepo.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).
		ifPresent(user->{
			blockAccessToken(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user,false,accessToken));
			blockRefreshToken(refreshTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user,false,refreshToken));
		});
		
		simpleResponseStructure.setMessage("revoked All Device Acess Sucessfully");
		simpleResponseStructure.setStatus(HttpStatus.ACCEPTED.value());
		return new ResponseEntity<ResponseStructure<SimpleResponseStructure>> (HttpStatus.OK);
	}
	

	
	private void blockAccessToken(List<AccessToken> accessToken) {
		accessToken.forEach(at ->{
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});
	}
	
	private void blockRefreshToken(List<AccessToken> refreshToken) {
		refreshToken.forEach(rt ->{
			rt.setBlocked(true);
			refreshTokenRepo.save(rt);
		});
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(String accessToken, String refreshToken,
			HttpServletRequest request, HttpServletResponse response) {
		if(accessToken==null && refreshToken==null) throw new UserNotLoggedInException("User has not logged in due to tha"
				+ "t this will not perform any kind of operation");
		String at = null;
		String rt = null;
		//		Cookie[] cookies=request.getCookies();
		//		for (Cookie c : cookies) {
		//			if(c.getName().equals("rt")) rt=c.getValue();
		//			if(c.getName().equals("at")) at=c.getValue();
		//		}
		accessTokenRepo.findByToken(at).ifPresent(access ->{
			access.setBlocked(true);
			accessTokenRepo.save(access);
		});
		refreshTokenRepo.findByToken(rt).ifPresent(refresh ->{
			refresh.setBlocked(true);
			refreshTokenRepo.save(refresh);
		});
		response.addCookie(cookieManager.invalidate(new Cookie(at,value)));
		response.addCookie(cookieManager.invalidate(new Cookie(rt, "")));
		authStructure.setMessage("Logout SucessFull");
		authStructure.setStatus(HttpStatus.OK.value());
		authStructure.setData(null);
		return new ResponseEntity<ResponseStructure<AuthResponse>>(authStructure,HttpStatus.OK);
	}

	private void grantAccess(HttpServletResponse response,User user) {
		//generating access and refress tokens
		String accessToken=jwtService.generateAccessToken(user.getUserName());
		String refreshToken= jwtService.generateAccessToken(user.getUserName());

		//adding access and refresh tokens cookies to the response
		response.addCookie(cookieManager.configure(accessExpiryInSeconds, new Cookie("at", accessToken)));
		response.addCookie(cookieManager.configure(refreshExpiryInSeconds, new Cookie("rt", refreshToken)));

		//saving the access and refresh cookie in to the database
		accessTokenRepo.save(AccessToken.builder()
				.token(accessToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());

		refreshTokenRepo.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
				.build());
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
