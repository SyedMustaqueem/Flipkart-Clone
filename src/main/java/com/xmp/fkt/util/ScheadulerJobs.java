package com.xmp.fkt.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xmp.fkt.repository.UserRepo;
import com.xmp.fkt.service.AuthService;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ScheadulerJobs {
	
	UserRepo userRepo;
private AuthService authService;

@Scheduled(cron = "0 0 0 * * *") // Daily at midnight
public void deleteNonVerifiedUsers()
{
    userRepo.findByIsEmailverified(false).forEach((user)->userRepo.delete(user));
}
}
