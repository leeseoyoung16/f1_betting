package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.user.LoginRequestDto;
import com.wowa.f1betting.dto.user.SignupRequestDto;
import com.wowa.f1betting.dto.user.UserRankingDto;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        SignupRequestDto dto = new SignupRequestDto("test", "1234");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        Long newId = userService.signup(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복 username시 예외 발생")
    void signupDuplicateUsername() {
        SignupRequestDto dto = new SignupRequestDto("test", "1234");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USERNAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        LoginRequestDto dto = new LoginRequestDto("userA", "pw");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User("userA", encoder.encode("pw"), 100000);

        when(userRepository.findByUsername("userA"))
                .thenReturn(Optional.of(user));

        User result = userService.login(dto);

        assertThat(result.getUsername()).isEqualTo("userA");
    }

    @Test
    @DisplayName("비밀번호 틀리면, LOGIN_FAILED 발생")
    void loginWrongPassword() {
        LoginRequestDto dto = new LoginRequestDto("userA", "wrong");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User("userA", encoder.encode("pw"), 100000);

        when(userRepository.findByUsername("userA"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("username 없을 떄, LOGIN_FAILED 발생")
    void loginUserNotFound() {

        when(userRepository.findByUsername("nope"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.login(new LoginRequestDto("nope", "pw"))
        )
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("사용자 ID 조회 성공")
    void getUserByIdSuccess() {
        User user = new User("tester", "pw", 500);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result.getUsername()).isEqualTo("tester");
    }

    @Test
    @DisplayName("존재하지 않는 userId일 때, USER_NOT_FOUND")
    void getUserByIdNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("랭킹 정렬 테스트")
    void getUserRanking() {

        User u1 = new User("a", "pw", 3000);
        User u2 = new User("b", "pw", 8000);
        User u3 = new User("c", "pw", 1000);

        when(userRepository.findAllByOrderByBalanceDesc())
                .thenReturn(List.of(u2, u1, u3)); // balance 내림차순

        List<UserRankingDto> ranking = userService.getUserRanking();

        assertThat(ranking.get(0).balance()).isEqualTo(8000);
        assertThat(ranking.get(1).balance()).isEqualTo(3000);
        assertThat(ranking.get(2).balance()).isEqualTo(1000);
    }

    @Test
    @DisplayName("유저 잔액 조회 성공")
    void getUserBalanceSuccess() {
        User user = new User("test", "pw", 9999);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Long balance = userService.getUserBalance(1L);

        assertThat(balance).isEqualTo(9999);
    }

    @Test
    @DisplayName("잔액 조회시, 유저 없으면 USER_NOT_FOUND")
    void getUserBalanceNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserBalance(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
