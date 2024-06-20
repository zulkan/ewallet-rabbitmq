package com.zulkan.ewallet.filter;


import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    @Autowired
    public AuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void setAuth(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return;
        }
        final String []token = authHeader.split(" ");
        if (token.length != 2 || !token[0].equals("Bearer")) {
            return;
        }
        // Get user identity and set it on the spring security context
        User user = userRepository.getUserByToken(token[1]);

        if (user == null) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, token,
                List.of()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        setAuth(request);
        chain.doFilter(request, response);
    }

}