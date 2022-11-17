package com.app.chatserver.controllers;

import com.app.chatserver.dto.ResponseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.app.chatserver.exceptions.RegistrationException;
import com.app.chatserver.exceptions.SmsException;
import com.app.chatserver.exceptions.TokenException;
import com.app.chatserver.exceptions.UserException;

@ControllerAdvice
@Log4j2
public class AdviceController {
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ResponseException> handleRegistrationException(RegistrationException e) {
	    log.warn(e.getMessage());
		return new ResponseEntity<>(
                new ResponseException(
                        e.getMessage(),
                        "Registration error."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ResponseException> handleRegistrationException(TokenException e) {
        log.warn(e.getMessage());
		return new ResponseEntity<>(
                new ResponseException(
                        e.getMessage(),
                        "Token error."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ResponseException> handleRegistrationException(SmsException e) {
        log.warn(e.getMessage());
		return new ResponseEntity<>(
                new ResponseException(
                        e.getMessage(),
                        "Error with sms code"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ResponseException> handleRegistrationException(UserException e) {
        log.warn(e.getMessage());
		return new ResponseEntity<>(
                new ResponseException(
                        e.getMessage(),
                        "User error"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
