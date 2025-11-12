package com.cluewave.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for creating and validating JSON Web Tokens (JWTs).
 *
 * <p>
 * This implementation uses the modern JJWT 0.12 API. Several setter-style
 * methods used in older examples (e.g. {@code setSubject}, {@code setIssuedAt},
 * {@code setExpiration}, {@code setSigningKey}, {@code parseClaimsJws},
 * {@code getBody}) have been deprecated in favour of shorter, more fluent
 * alternatives. See the JJWT migration guide for details.
 * </p>
 */
@Component
public class JwtUtils {

    /**
     * Symmetric key used to sign and verify tokens. Stored as a {@link SecretKey}
     * because the newer JJWT API distinguishes between signing and verification
     * keys for improved type safety. The key is generated once from the
     * base64‑encoded secret configured in {@code application.properties}.
     */
    private final SecretKey signingKey;

    /**
     * Number of milliseconds after which a generated token should expire.
     */
    private final long jwtExpirationMs;

    /**
     * Constructs the {@code JwtUtils} with the provided base64‑encoded secret
     * and expiration period.  The secret is decoded and converted into a
     * {@link SecretKey} using {@link Keys#hmacShaKeyFor(byte[])}.  When using
     * HMAC keys, JJWT will infer the appropriate signature algorithm from the
     * key's algorithm and length when {@link #signWith(SecretKey)} is used.
     *
     * @param jwtSecret      base64‑encoded secret used to sign tokens
     * @param jwtExpirationMs expiration time in milliseconds
     */
    public JwtUtils(@Value("${jwt.secret}") String jwtSecret,
                    @Value("${jwt.expiration-ms}") long jwtExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generates a cryptographically signed JWT containing the supplied user's
     * username as the subject. The token will include standard JWT claims such
     * as the issued-at timestamp and an expiration timestamp calculated from
     * {@link #jwtExpirationMs}.  Deprecated methods such as
     * {@code setSubject}, {@code setIssuedAt} and {@code setExpiration} are
     * replaced with their fluent counterparts {@code subject()},
     * {@code issuedAt()} and {@code expiration()} respectively.  Signing is
     * performed with the key alone – JJWT will choose the appropriate
     * HMAC‑SHA algorithm based on the key; therefore there is no need to
     * specify {@code SignatureAlgorithm.HS256}.
     *
     * @param userDetails the authenticated user for whom to generate a token
     * @return a compact JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the username (subject) from a cryptographically signed JWT.  The
     * newer API uses {@link io.jsonwebtoken.JwtParserBuilder#verifyWith(SecretKey)}
     * instead of {@code setSigningKey}.  Parsing signed claims is done via
     * {@link io.jsonwebtoken.JwtParser#parseSignedClaims(CharSequence)}, and
     * the resulting {@link io.jsonwebtoken.Jws} payload can be retrieved via
     * {@link io.jsonwebtoken.Jwt#getPayload()}.
     *
     * @param token the JWT string to inspect
     * @return the subject claim (username) or {@code null} if parsing fails
     */
    /**
     * Extract the username (subject) from the supplied token.
     *
     * <p>Returns {@code null} if the token cannot be parsed or is invalid.</p>
     */
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception ex) {
            // Parsing failed or token was not signed correctly
            return null;
        }
    }

    /**
     * Validates the given JWT by attempting to parse its signed claims using
     * {@link #signingKey}.  If parsing succeeds, the token is considered valid.
     * Otherwise, any exception (including signature verification failure or
     * expiration) will result in a {@code false} response.
     *
     * @param token the JWT to validate
     * @return {@code true} if the token is a valid signed JWT and not expired
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns the configured JWT expiration duration in milliseconds.
     *
     * @return token expiration in milliseconds
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}