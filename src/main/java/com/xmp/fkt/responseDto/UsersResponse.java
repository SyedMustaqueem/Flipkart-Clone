package com.xmp.fkt.responseDto;

import com.xmp.fkt.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsersResponse {
	 private int userId;
	 private String userName;
	 private String email;
	 private UserRole userRole;
	 private boolean isEmailVerified;
	 private boolean idDeleted;
}
