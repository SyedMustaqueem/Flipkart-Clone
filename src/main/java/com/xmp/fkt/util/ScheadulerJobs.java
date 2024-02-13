package com.xmp.fkt.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xmp.fkt.entity.AccessToken;
import com.xmp.fkt.entity.RefreshToken;
import com.xmp.fkt.repository.AccessTokenRepo;
import com.xmp.fkt.repository.RefreshTokenRepo;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ScheadulerJobs {

	private AccessTokenRepo accessTokenRepo;

	private RefreshTokenRepo refreshTokenRepo;

	// Scheduled task to run every 6 hours
	@Scheduled(cron = "0 */6 * * * *") // Run every 6 hours
	public void cleanUpAllTheExpiredToken()
	{
		LocalDateTime now = LocalDateTime.now();
		List<AccessToken> accessTokenList = accessTokenRepo.findByExpirationBefore(now);
		accessTokenRepo.deleteAll(accessTokenList);
		
		List<RefreshToken> refreshTokenList = refreshTokenRepo.findByExpirationBefore(now);
		refreshTokenRepo.deleteAll(refreshTokenList);

	}

}