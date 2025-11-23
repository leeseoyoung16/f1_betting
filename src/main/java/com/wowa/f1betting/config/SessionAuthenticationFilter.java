package com.wowa.f1betting.config;

import com.wowa.f1betting.controller.AuthController;
import com.wowa.f1betting.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.startsWith("/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session != null) {

            Object sessionUserId = session.getAttribute(AuthController.LOGIN_USER_ID);

            if (sessionUserId instanceof Long userId) {

                Authentication existingAuth = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

                if (existingAuth == null || !existingAuth.isAuthenticated()) {

                    userRepository.findById(userId).ifPresent(user -> {

                        UserDetails userDetails =
                                org.springframework.security.core.userdetails.User.builder()
                                        .username("USER_ID:" + user.getId())
                                        .password("")
                                        .roles(user.getRole().name())
                                        .build();

                        Authentication authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
