package com.fraud.controller;

import com.fraud.model.AuthRequest;
import com.fraud.model.AuthResponse;
import com.fraud.service.UserService;
import com.fraud.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "API for Authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login")
    @Async
    public CompletableFuture<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            logger.info("Authentication successful for user: {}", authRequest.getUsername());

            final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtTokenUtil.generateToken(userDetails);

            return CompletableFuture.completedFuture(ResponseEntity.ok(new AuthResponse(jwt)));
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {}", authRequest.getUsername());
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Incorrect username or password")));
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for user: {}", authRequest.getUsername(), e);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("An unexpected error occurred. Please try again later.")));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout")
    @Async
    public CompletableFuture<ResponseEntity<String>> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7);
                jwtTokenUtil.invalidateToken(jwtToken);
                logger.info("Logout successful for token: {}", jwtToken);
                return CompletableFuture.completedFuture(ResponseEntity.ok("Logged out successfully"));
            } else {
                logger.warn("Invalid logout request: Missing or malformed Authorization header");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid request"));
            }
        } catch (Exception e) {
            logger.error("An unexpected error occurred during logout", e);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later."));
        }
    }
}
