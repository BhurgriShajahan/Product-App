package com.product.controller;

import com.product.dto.UserDto;
import com.product.dto.request.LoginRequest;
import com.product.dto.response.AuthResponse;
import com.product.securityConfig.JwtUtil;
import com.product.securityConfig.MyUserDetailsService;
import com.product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final MyUserDetailsService userDetailsService;
    @Autowired


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, MyUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody UserDto userDto) {

        // Step 1: Register the user
        userService.signup(userDto);

        // Step 2: Authenticate the user to generate the JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());
        System.out.println("Token is----->" + token);

        // Step 3: Return the token with a success message in the response
        String successMessage = "Signup successful. You can now log in using the token.";
        return new AuthResponse(token, successMessage);  // Return token and success message to the user
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Generate a new token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Return the token in the response
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid username or password"));
        }
    }

}
