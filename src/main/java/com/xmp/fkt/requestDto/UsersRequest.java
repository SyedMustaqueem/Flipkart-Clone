package com.xmp.fkt.requestDto;

import com.xmp.fkt.enums.UserRole;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersRequest {
	
	// private String userName;
	 @Column(unique = true)
	 private String email;
	 @Column(unique = true)
	 private String password;
	 private UserRole userRole;
	
}
