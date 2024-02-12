package com.xmp.fkt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xmp.fkt.entity.RefreshToken;
import com.xmp.fkt.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long>{

	Object blockToken(String refreshToken);

	Optional<RefreshToken> findByToken(String rt);

}
