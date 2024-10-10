package darts.ng.io.usersMicroservice.security;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.utilities.CustomRuntimeException;
import darts.ng.io.usersMicroservice.utilities.JwtValidationException;
import darts.ng.io.usersMicroservice.utilities.ErrorHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class FilterService {

    private static final String SECRET_KEY = "PciAAE1MF5lVHhZE3MwvMJHp9bAiiEA8mva2qTF0e+s=";
    private static final long TOKEN_EXPIRATION_TIME_MS = 1000 * 60 * 60;
    private final UserDetailsService userDetailsService;
    private SecretKey secretKey;

    public FilterService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Generate a Secret Key
    private SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
        return secretKey;
    }

    // Extract username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract specific claims from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate token with additional claims
    public String generateToken(Map<String, Object> extraClaims, String uuid, String email, String organisationId) {

        extraClaims.put("uuid", uuid);
        extraClaims.put("email", email);
        extraClaims.put("organisationId", organisationId);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_MS))
                .signWith(getSigningKey())
                .compact();
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && verifyTokenSignature(token));
        } catch (JwtValidationException ex) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Verify token signature
    public boolean verifyTokenSignature(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "error", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void resetUserAccessToken(UsersDatabaseModel fetchedSavedRecordFromDB) {
        // Custom implementation
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Method to extract userId from the token
    public String extractUUID(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(extractTokenFromHeader(token))
                .getPayload();
        return claims.get("uuid", String.class);
    }

    // Method to extract user email from the token
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(extractTokenFromHeader(token))
                .getPayload();
        return claims.get("email", String.class);
    }

    // Method to extract organisationId from the token
    public String extractOrganisationId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(extractTokenFromHeader(token))
                .getPayload();
        return claims.get("organisationId", String.class);
    }

    // Create Authentication from the token
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(extractTokenFromHeader(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String jwtToken(String uiid, String email, String organisationId) {
        return generateToken(new HashMap<>(), uiid, email, organisationId);
    }

    public String extractTokenFromHeader(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

}
