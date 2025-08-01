package com.example.techmarket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lucio
 */

@Component
public class InactivityTimeoutFilter extends OncePerRequestFilter {
    private static final long TIMEOUT = 20 * 60 * 1000L;
    private static final Map<String, Long> lastActivityMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated() && authentication.getName() != null){
            String userKey = authentication.getName();

            Long lastActivity = lastActivityMap.get(userKey);
            long now = Instant.now().toEpochMilli();

            if(lastActivity != null && (now - lastActivity) > TIMEOUT){
                lastActivityMap.remove(userKey);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Session expired due to inactivity");
                return;
            }

            lastActivityMap.put(userKey, now);
        }
        filterChain.doFilter(request, response);

    }
}
