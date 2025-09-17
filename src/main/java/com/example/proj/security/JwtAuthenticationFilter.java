package com.example.proj.security;

import com.example.proj.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component  // Make this a Spring Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;  // Declare JwtUtil as final and inject via constructor

    // Constructor injection - no need for @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if the Authorization header exists and starts with "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);  // Extract token
            username = jwtUtil.extractUsername(token);  // Extract username from token

            // If username exists and the token is valid, set the authentication in the security context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtUtil.isTokenValid(token, username)) {

                // Create an authentication object (you can customize authorities if needed)
                CustomAuthenticationToken authentication = new CustomAuthenticationToken(username, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Clean up if necessary (typically not needed for filters)
    }
}