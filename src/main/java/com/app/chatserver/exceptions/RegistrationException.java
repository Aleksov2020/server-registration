package com.app.chatserver.exceptions;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String message){
        super(message);
    }
}
