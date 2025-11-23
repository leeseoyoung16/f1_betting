package com.wowa.f1betting.controller;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.user.LoginRequestDto;
import com.wowa.f1betting.dto.user.LoginResponseDto;
import com.wowa.f1betting.dto.user.SignupRequestDto;
import com.wowa.f1betting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    public static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    private final UserService userService;

    @PostMapping("/signup")
    public Long signup(@RequestBody SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto,
                                  HttpServletRequest request) {

        User user = userService.login(requestDto);

        HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_USER_ID, user.getId());

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username("USER_ID:" + user.getId())
                        .password("")
                        .roles("USER")
                        .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getBalance()
        );
    }

}