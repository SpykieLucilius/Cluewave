package com.cluewave.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key signingKey;
    private final long jwtExpirationMs;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret,
                    @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generates a new JWT for the supplied {@link UserDetails}. The token's
     * subject will be the user's username (email) and the expiration will be
     * computed based on the configured duration.
     *
     * @param userDetails the authenticated user
     * @return a signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (email) from the given JWT.
     *
     * For your jjwt version, Jwts.parser() returns a builder, so we must call
     * .build() before parsing.
     *
     * @param token the JWT
     * @return the subject contained within the token
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()                
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates the given JWT for integrity and expiration.
     *
     * @param token the JWT to validate
     * @return whether the token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()              
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
