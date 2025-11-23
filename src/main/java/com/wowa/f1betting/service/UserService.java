package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.user.LoginRequestDto;
import com.wowa.f1betting.dto.user.SignupRequestDto;
import com.wowa.f1betting.dto.user.UserRankingDto;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private int INITIAL_BALANCE = 100_000;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public Long signup(SignupRequestDto signupRequestDto) {

        userRepository.findByUsername(signupRequestDto.username())
                .ifPresent(user -> {
                    throw new CustomException(ErrorCode.USERNAME_DUPLICATED);
                });

        String pwHash = bCryptPasswordEncoder.encode(signupRequestDto.password());
        User user = new User(signupRequestDto.username(), pwHash, INITIAL_BALANCE);

        userRepository.save(user);
        return user.getId();
    }


    public User login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!bCryptPasswordEncoder.matches(loginRequestDto.password(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        return user;
    }


    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


    public List<UserRankingDto> getUserRanking() {
        return userRepository.findAllByOrderByBalanceDesc().stream()
                .map(u -> new UserRankingDto(
                        u.getId(),
                        u.getUsername(),
                        u.getBalance()
                ))
                .toList();
    }


    public Long getUserBalance(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getBalance();
    }
}

