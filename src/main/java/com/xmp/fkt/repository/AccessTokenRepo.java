package com.xmp.fkt.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xmp.fkt.entity.AccessToken;
import com.xmp.fkt.entity.User;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {


	Optional<AccessToken> findByToken(String at);

	List<AccessToken> findByExpirationBefore(LocalDateTime now);

	Optional<AccessToken> findByUserAndIsBlocked(User user, boolean b);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b,String token);

	List<AccessToken> findAllByUserAndIsBlocked(User user, boolean b);

}
