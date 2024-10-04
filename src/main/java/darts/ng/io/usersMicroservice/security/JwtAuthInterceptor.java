package darts.ng.io.usersMicroservice.security;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.grpc.Status;


@Component
public class JwtAuthInterceptor implements ServerInterceptor {

    private final JwtService jwtService;
    private final ApplicationContext context;

    public JwtAuthInterceptor( JwtService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

       try{

           String authHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));

           if (authHeader == null || authHeader.isEmpty()) {
               call.close(Status.UNAUTHENTICATED.withDescription("Missing JWT token"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           String token = extractTokenFromHeader(authHeader);

           if (token == null) {
               call.close(Status.UNAUTHENTICATED.withDescription("Invalid JWT token format"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           if (!jwtService.verifyTokenSignature(token)) {
               call.close(Status.UNAUTHENTICATED.withDescription("Invalid token signature"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           String username = jwtService.extractUsername(token);

           UserDetails userDetails = loadUserDetails(username);

           if (userDetails == null) {
               call.close(Status.UNAUTHENTICATED.withDescription("User not found"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           if (!validateToken(token, userDetails)) {
               call.close(Status.UNAUTHENTICATED.withDescription("Invalid JWT token"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           if (jwtService.isTokenExpired(token)) {
               call.close(Status.UNAUTHENTICATED.withDescription("Token expired"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           if (isTokenMalformed(token)) {
               call.close(Status.UNAUTHENTICATED.withDescription("Token malformed"), new Metadata());
               return new ServerCall.Listener<ReqT>() {};
           }

           return next.startCall(call, headers);
       } catch (Exception e) {
           call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), new Metadata());
           return new ServerCall.Listener<ReqT>() {};
       }
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    }

    private UserDetails loadUserDetails(String username) {
        return context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.validateToken(token, userDetails);
    }

    private boolean isTokenMalformed(String token) {
        try {
            jwtService.validateToken(token, loadUserDetails(jwtService.extractUsername(token)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}