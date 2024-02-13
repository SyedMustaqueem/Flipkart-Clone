package com.xmp.fkt.util;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class SimpleResponseStructure {
	private int status;
private String message;


}