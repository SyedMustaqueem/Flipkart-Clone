package com.xmp.fkt.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xmp.fkt.entity.AccessToken;
import com.xmp.fkt.entity.RefreshToken;
import com.xmp.fkt.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String rt);

	List<RefreshToken> findByExpirationBefore(LocalDateTime now);

	Optional<RefreshToken> findByUserAndIsBlocked(User user, boolean b);

	void save(AccessToken rt);

	List<AccessToken> findAllByUserAndIsBlocked(User user, boolean b);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String tokenoken);

}
