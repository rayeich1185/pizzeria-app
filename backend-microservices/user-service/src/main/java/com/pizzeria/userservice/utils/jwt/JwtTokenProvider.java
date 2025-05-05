package com.pizzeria.userservice.utils.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.pizzeria.userservice.utils.configs.UserPrincipal;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecretString.getBytes(StandardCharsets.UTF_8);
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
        logger.info("JWT secret key initialized");
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parser().setSigningKey(jwtSecretKey).build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
