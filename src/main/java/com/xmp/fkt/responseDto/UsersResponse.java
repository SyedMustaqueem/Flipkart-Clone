package com.xmp.fkt.responseDto;

import java.sql.Time;

import com.xmp.fkt.enums.UserRole;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsersResponse {
	 private int userId;
	 private String userName;
	 private String email;
	 private UserRole userRole;
	 private boolean isEmailVerified;
	 private boolean idDeleted;
}
