package com.app.chatserver.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.eatthepath.otp.HmacOneTimePasswordGenerator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService{
	private SecretKey sKey;
	@Getter private String smsCode;
	@Value("${hay.apiKey.smsService}")
	private String api_key;
	@Value("${hay.secret.counter}")
	private Integer secretCounter;
	@Value("${hay.secret.smsService}")
	private String secretKey;

	@Override
	public String generateSmsCode(String signature) throws InvalidKeyException, NoSuchAlgorithmException {
		sKey = new SecretKeySpec((signature + secretKey).getBytes(), "AES");
		final HmacOneTimePasswordGenerator hotp = new HmacOneTimePasswordGenerator(6);
		this.smsCode = hotp.generateOneTimePasswordString(this.sKey, secretCounter + 0);
		return this.smsCode;
	}
	@Override
	public void sendSmsCode(String phone) throws IOException {
		String generate_url = 
				"https://smspilot.ru/api.php?send="	+ URLEncoder.encode("Ваш код - " + this.smsCode, "UTF-8")
				+ "&to=" + URLEncoder.encode(phone, "UTF-8")
				+ "&apikey=" + URLEncoder.encode(api_key, "UTF-8")
				+ "&format=json";
		final URL url = new URL(generate_url);
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		//maybe delete and rename method type to void
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			final StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}
