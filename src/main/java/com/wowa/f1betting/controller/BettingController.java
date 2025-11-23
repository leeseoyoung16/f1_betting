package com.wowa.f1betting.controller;

import com.wowa.f1betting.domain.bet.BettingRecord;
import com.wowa.f1betting.dto.bet.BettingRecordResponseDto;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.dto.bet.BettingRecordRequestDto;
import com.wowa.f1betting.repository.UserRepository;
import com.wowa.f1betting.service.BettingService;
import com.wowa.f1betting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bet")
public class BettingController {

    private final BettingService bettingService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/place")
    public ResponseEntity<BettingRecordResponseDto> placeBet(
            @RequestBody BettingRecordRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(
                userDetails.getUsername().replace("USER_ID:", "")
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BettingRecord record = bettingService.placeBet(requestDto, userId);

        BettingRecordResponseDto response = BettingRecordResponseDto.from(record);

        return ResponseEntity.ok(response);
    }
}