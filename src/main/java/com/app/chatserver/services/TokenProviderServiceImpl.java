package com.app.chatserver.services;

import com.app.chatserver.exceptions.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
@Log4j2
public class TokenProviderServiceImpl implements TokenProviderService {
    private @Getter @Setter String tokenProvider;
    @Value("${hay.jwtExpirationMs}")
    private Integer expiration;
    @Value("${hay.TokenProviderSecret}")
    private String secretForProvider;
    public String generateTokenProvider(String phone, String userName){
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuer(DEFAULT_ISSUER)
                .setSubject(DEFAULT_SUBJECT)
                .claim("phone", phone)
                .claim("name", userName)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + this.expiration))
                .signWith(
                        SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode(this.secretForProvider) //maybe change this
                )
                .compact();
    }
    public Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.decode(this.secretForProvider))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Could not get all claims Token from passed token");
            claims = null;
        }
        return claims;
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.decode(this.secretForProvider))
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
}
