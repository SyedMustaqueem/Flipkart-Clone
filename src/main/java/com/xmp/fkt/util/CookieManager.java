package com.xmp.fkt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
@Component
public class CookieManager {

	@Value("${myapp.Domain}")
	private String domain;

	public Cookie configure(int expirationInSeconds,Cookie cookie) {
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(expirationInSeconds);
		//cookie.setValue(data);
		return cookie;
	}
	
	public Cookie invalidate(Cookie cookie) {
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
		
	}
}
