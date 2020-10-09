package com.example.poller_bear.security;

import com.example.poller_bear.model.AccountUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.JWTSecret}")
    private String secret;

    @Value("${app.JWTExpirationInMillisecond}")
    private Long jwtExpirationTimeInMillisecond;

    public Long extractUserId(String jwtToken) {

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken);

        return Long.parseLong(claims.getBody().getSubject());
    }

    public boolean validate(String jwtToken) {

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException exception) {
            log.error(exception.getMessage());
        } catch (JwtException exception) {
            System.out.println(exception);
            log.error(exception.getMessage());
        }
        return false;
    }

    public String createToken(Authentication authentication) {

        Date expirationTime = new Date(new Date().getTime() + jwtExpirationTimeInMillisecond);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        AccountUserDetails user = (AccountUserDetails) authentication.getPrincipal();

        return Jwts
                .builder()
                .setIssuer("server")
                .setAudience("you")
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
