package com.zulkan.ewallet.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zulkan.ewallet.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Random;
public class Utils {
    private static final Random random = new Random();

    private static final ObjectMapper objectMapper = new ObjectMapper();


    private Utils() {
    }

    public static List<GrantedAuthority> toGrantedAuthority(List<String> authorities) {
        return authorities.stream().map(
                authority -> (GrantedAuthority) () -> authority
        ).toList();
    }


    public static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getUserFromContext() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
