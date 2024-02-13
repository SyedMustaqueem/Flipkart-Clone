package com.xmp.fkt.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xmp.fkt.entity.AccessToken;
import com.xmp.fkt.repository.AccessTokenRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{

	private AccessTokenRepo accessTokenRepo;

	private JwtService jwtService;

	private CustomUserDetailsService userDetailsService;


	@Override
	@SuppressWarnings("unused")
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String at=null;
		String rt=null;
		Cookie[] cookies=request.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals("at")) at=cookie.getValue();
			if(cookie.getName().equals("rt")) at=cookie.getValue();
		}
		String userName=null;
		if(at !=null && rt!=null) {
			//if(at==null||rt==null) throw new RuntimeException("User not logged In");	
			Optional<AccessToken> accessToken = accessTokenRepo.findByTokenAndIsBlocked(at,false);
			if(accessToken==null)  throw new RuntimeException("User not logged In");
			else{
				log.info("authenticating the token");
				userName = jwtService.extractUserName(at);
				if(userName==null) throw new RuntimeException("Failed to Autheticate user name Not Found");
				UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
				UsernamePasswordAuthenticationToken token = 
						new UsernamePasswordAuthenticationToken(userName, null ,userDetails.getAuthorities());
				token.setDetails(new WebAuthenticationDetails(request));
				SecurityContextHolder.getContext().setAuthentication(token);
				log.info("authenticated the token Sucessfull");
			}
			filterChain.doFilter(request, response);
		}
	}
}
