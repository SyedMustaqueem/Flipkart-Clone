package com.xmp.fkt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xmp.fkt.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

	void findByUserName(String name);

	Object blockToken(String accessToken);

	Optional<AccessToken> findByToken(String at);

}
