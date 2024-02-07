package com.xmp.fkt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xmp.fkt.entity.Seller;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer>{

}
