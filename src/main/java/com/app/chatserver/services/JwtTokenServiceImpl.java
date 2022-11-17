package com.app.chatserver.services;

import java.util.Date;

import com.app.chatserver.exceptions.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class JwtTokenServiceImpl implements JwtTokenService{
	@Getter @Setter private String jwtToken;
    @Value("${hay.jwtExpirationMs}")
    private int expiration;
    @Value("${hay.jwtSecret}")
    private String secret;
    @Override
	public String createJwtAuthTokenByUserId(Integer id) {
		Long now = System.currentTimeMillis();
		this.jwtToken = Jwts.builder()
				  .setIssuer(DEFAULT_ISSUER)
				  .setSubject(DEFAULT_SUBJECT)
				  .claim("id", id)
				  .setIssuedAt(new Date(now))
				  .setExpiration(new Date(now + this.expiration))
				  .signWith(
                        SignatureAlgorithm.HS256,
				        TextCodec.BASE64.decode(this.secret) //maybe change this
				  )
				  .compact();
		return jwtToken;
	}
	@Override
	public Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Could not get all claims Token from passed token");
            claims = null;
        }
        return claims;
    }
	@Override
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.decode(this.secret))
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            throw new TokenException(ERROR_INVALID_JWT_SIGNATURE);
        } catch (MalformedJwtException ex) {
            throw new TokenException(ERROR_INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new TokenException(ERROR_EXPIRED_TOKEN);
        } catch (UnsupportedJwtException ex) {
            throw new TokenException(ERROR_UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException ex) {
            throw new TokenException(ERROR_CLAIMS_IS_EMPTY);
        }
    }
    @Override
    public String getSignature(String token) {
    	return token.split("\\.")[2];
    }
}
