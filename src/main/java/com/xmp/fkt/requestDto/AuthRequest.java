package com.xmp.fkt.requestDto;

import com.xmp.fkt.enums.UserRole;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequest {
private String email;
private String password;
}
