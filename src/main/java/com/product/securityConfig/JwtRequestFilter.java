package com.product.securityConfig;

import com.product.exceptions.CustomJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;
    private final MyUserDetailsService myUserDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            try {
                // Attempt to extract username from the token
                Claims claims = jwtUtil.getClaims(jwt);
                username = claims.getSubject();
            } catch (ExpiredJwtException e) {
                handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired. Please login again.", e);
                return;
            } catch (CustomJwtException e) {
                handleException(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage(), e);
                return;
            } catch (Exception e) {
                handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token.", e);
                return;
            }
        }

        // Continue processing if username is extracted and token is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);

            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, int status, String message) throws IOException {
        handleException(response, status, message, null);
    }

    private void handleException(HttpServletResponse response, int status, String message, Exception e) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
        response.getWriter().flush();
        if (e != null) {
            LOGGER.error(message, e);
        } else {
            LOGGER.info(message);
        }
    }
}
