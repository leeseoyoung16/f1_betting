package com.wowa.f1betting.controller;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.user.UserRankingDto;
import com.wowa.f1betting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/ranking")
    public List<UserRankingDto> getRanking() {
        return userService.getUserRanking();
    }
}

