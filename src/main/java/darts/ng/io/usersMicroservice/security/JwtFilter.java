package darts.ng.io.usersMicroservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final FilterService jwtService;
    private final ApplicationContext context;

    private static final Set<String> OPEN_ENDPOINTS = new HashSet<>();

    static {
        OPEN_ENDPOINTS.add("/api/account/register");
        OPEN_ENDPOINTS.add("/api/account/send-confirmation-email");
        OPEN_ENDPOINTS.add("/api/account/confirm-email");
        OPEN_ENDPOINTS.add("/api/auth/login");
        OPEN_ENDPOINTS.add("/api/auth/request-password-reset");
        OPEN_ENDPOINTS.add("/api/auth/reset-password");
    }

    public JwtFilter(FilterService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    private static final int UNAUTHORIZED_STATUS = HttpServletResponse.SC_UNAUTHORIZED;
    private static final String MISSING_AUTH_HEADER_ERROR = "{\"status\":false,\"error\":\"Missing Authorization header\"}";
    private static final String INVALID_AUTH_HEADER_FORMAT_ERROR = "{\"status\":false,\"error\":\"Invalid Authorization header format\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Skip authentication for open endpoints
        if (OPEN_ENDPOINTS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            respondUnauthorized(response, MISSING_AUTH_HEADER_ERROR);
            return;
        }

        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            respondUnauthorized(response, INVALID_AUTH_HEADER_FORMAT_ERROR);
        }

        try {
            String email = jwtService.extractUsername(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = loadUserDetails(email);
                if (validateToken(token, userDetails)) {
                    System.out.println("sjss "+userDetails.getPassword()+" "+userDetails.getUsername());
                    authenticateUser(userDetails, request);
                }
            }
            System.out.println("sjss "+"sk");
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            respondUnauthorized(response, e.getMessage());
        }
    }


    private void respondUnauthorized(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(UNAUTHORIZED_STATUS);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":false,\"error\":\"" +errorMessage+ "\"}");
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    private UserDetails loadUserDetails(String email) {
        return context.getBean(CustomUserDetailsService.class).loadUserByUsername(email);
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.validateToken(token, userDetails);
    }

    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
