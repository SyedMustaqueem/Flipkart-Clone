package com.xmp.fkt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "seller")
public class Seller extends User {

}
