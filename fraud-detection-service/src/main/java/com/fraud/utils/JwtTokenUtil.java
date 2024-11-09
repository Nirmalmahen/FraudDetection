package com.fraud.utils;

import com.fraud.exception.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    Set<String> invalidTokens = new HashSet<>();
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationMs}")
    private long expirationMs;

    // Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("Error getting username from token", e);
            throw new JwtException("Error getting username from token",e);
        }
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getExpiration);
        } catch (Exception e) {
            logger.error("Error getting expiration date from token", e);
            throw new JwtException("Error getting expiration date from token",e);
        }
    }

    // Retrieve a single claim from the token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Error getting claim from token", e);
            throw new JwtException("Error getting claim from token",e);
        }
    }

    // Retrieve all claims from the token
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            logger.error("Error parsing claims from token", e);
            throw new JwtException("Error parsing claims from token",e);
        }
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error checking if token is expired", e);
            throw new JwtException("Error checking if token is expired",e);
        }
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        try {
            return doGenerateToken(userDetails.getUsername());
        } catch (Exception e) {
            logger.error("Error generating token for user", e);
            throw new JwtException("Error generating token for user",e);
        }
    }

    // While creating the token
    private String doGenerateToken(String subject) {
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        } catch (Exception e) {
            logger.error("Error during token creation", e);
            throw new JwtException("Error during token creation",e);
        }
    }

    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            boolean invalidToken = invalidTokens.contains(token);
            if (invalidToken) {
                logger.warn("Attempted use of invalidated token: {}", token);
                return false;
            }

            final String username = getUsernameFromToken(token);
            boolean tokenExpired = isTokenExpired(token);
            boolean usernameMatches = username.equals(userDetails.getUsername());

            if (tokenExpired) {
                logger.warn("Token expired for username: {}", username);
            }

            if (!usernameMatches) {
                logger.warn("Username mismatch: token username {} vs requested username {}", username, userDetails.getUsername());
            }

            return usernameMatches && !tokenExpired;
        } catch (Exception e) {
            logger.error("Error validating token", e);
            throw new JwtException("Error validating token",e);
        }
    }

    public void invalidateToken(String token) {
        try {
            invalidTokens.add(token);
            logger.info("Token invalidated successfully: {}", token);
        } catch (Exception e) {
            logger.error("Error invalidating token: {}", token, e);
            throw new JwtException("Error invalidating token",e);
        }
    }
}
