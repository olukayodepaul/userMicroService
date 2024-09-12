package darts.ng.io.usersMicroservice.security;

import darts.ng.io.usersMicroservice.util.AuthHeaderUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext context;

    private static final Set<String> OPEN_ENDPOINTS = new HashSet<>();

    static {
        OPEN_ENDPOINTS.add("/api/auth/login");
    }

    public JwtFilter(JwtService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String requestURI = request.getRequestURI();

        // Skip authentication for open endpoints
        if (OPEN_ENDPOINTS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        authHeader = AuthHeaderUtils.cleanAuthorizationHeader(authHeader);

        if (authHeader == null || authHeader.isEmpty()) {
            // Handle missing or empty Authorization header for non-open endpoints
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":false,\"error\":\"Missing Authorization header\"}");
            return;
        }

        if (authHeader == null || authHeader.isEmpty()) {
            // Handle missing or empty Authorization header
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":false,\"error\":\"Missing Authorization header\"}");
            return;
        }

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract token (remove Bearer)
                token = authHeader.substring(7).trim();
                log.info("Token after removing Bearer: {}", token);

                // Extract username from token
                username = jwtService.extractUserName(token);
                log.info("Extracted username: {}", username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication successful for user: {}", username);
                }
            }

            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":false,\"message\":\"error detected\",\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

