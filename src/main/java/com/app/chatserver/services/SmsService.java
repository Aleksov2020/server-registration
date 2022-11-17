package com.app.chatserver.services;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface SmsService {
    String generateSmsCode(String signature) throws InvalidKeyException, NoSuchAlgorithmException;
    void sendSmsCode(String phone) throws IOException;

}
