package darts.ng.io.usersMicroservice.security;

import darts.ng.io.usersMicroservice.login.entity.LoginModel;
import darts.ng.io.usersMicroservice.util.CustomException;
import darts.ng.io.usersMicroservice.util.RegErrorHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;


@Service
public class JwtService {

    private static final String SECRET_KEY = "PciAAE1MF5lVHhZE3MwvMJHp9bAiiEA8mva2qTF0e+s=";
    private final UserDetailsService userDetailsService;
    private SecretKey secretKey;

    public JwtService(UserDetailsService userDetailsService) {
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
    public String generateToken(Map<String, Object> extraClaims, LoginModel loginModel) {
        return Jwts.builder()
                .claims(extraClaims)  // Add extra claims
                .subject(loginModel.getEmail())  // Set the subject as the email
                .issuedAt(new Date())  // Set the issued at date
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 20))  // Token valid for 20 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign the token with the secret key
                .compact();  // Generate the token
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }catch (ExpiredJwtException e) {
            throw new CustomException(
                    new RegErrorHandler(false, "Token has expired"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (SignatureException e) {
            throw new CustomException(
                    new RegErrorHandler(false, "Invalid JWT signature"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (MalformedJwtException e) {
            throw new CustomException(
                    new RegErrorHandler(false, "Malformed JWT token"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (IllegalArgumentException e) {
            throw new CustomException(
                    new RegErrorHandler(false, "JWT claims string is empty"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // Extract username from token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
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

    // Create Authentication from the token
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}



