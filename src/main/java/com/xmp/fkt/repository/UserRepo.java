package com.xmp.fkt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xmp.fkt.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

	Optional<User> findByUserName(String userName);

	void deleteByEmailVerifiedFalse();

}
