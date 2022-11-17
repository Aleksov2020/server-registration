package com.app.chatserver.dto;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.eatthepath.otp.HmacOneTimePasswordGenerator;

public class SecretKeyEntity {

	private final SecretKey key;

	/*
	 * Generate SecretKey
	 */
	public SecretKeyEntity() 
			throws NoSuchAlgorithmException {
		final HmacOneTimePasswordGenerator hotp = new HmacOneTimePasswordGenerator();
		final SecretKey key;
		{
			final KeyGenerator keyGenerator = KeyGenerator.getInstance(hotp.getAlgorithm());
			final int macLengthInBytes = Mac.getInstance(hotp.getAlgorithm()).getMacLength();
			keyGenerator.init(macLengthInBytes * 8);
			key = keyGenerator.generateKey();
		}

		this.key = key;
	}
	
	/*
	 * String -> SecretKey
	 */
	public SecretKeyEntity(String sKey) throws NoSuchAlgorithmException {
		System.out.print(sKey);
		byte[] decodedKey = Base64.getDecoder().decode(sKey);
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		this.key = originalKey;
	}

	public String toString() {
		return Base64.getEncoder().encodeToString(this.key.getEncoded());
	}

	public SecretKey getSecretKey() {
		return this.key;
	}
}
