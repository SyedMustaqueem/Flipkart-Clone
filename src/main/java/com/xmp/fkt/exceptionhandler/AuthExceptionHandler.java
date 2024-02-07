package com.xmp.fkt.exceptionhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler{

@Override
protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
	Map<String, String> mapErrors = new HashMap<>();
	List<ObjectError> objectErrors = ex.getAllErrors();
	for (ObjectError objectError : objectErrors) {
		FieldError fieldError = (FieldError) objectError;
		mapErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
	}
	return exceptionStructure(HttpStatus.BAD_REQUEST, ex.getMessage(), mapErrors);
}

private ResponseEntity<Object> exceptionStructure(HttpStatus status, String msg, Object rootCause) {
	return new ResponseEntity<Object>(Map.of("Status", status.value(), "Message", msg, "Root Cause", rootCause),
			status);
}

}
