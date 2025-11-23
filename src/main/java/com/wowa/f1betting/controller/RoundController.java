package com.wowa.f1betting.controller;

import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceStatus;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.error.CustomException;
import com.wowa.f1betting.error.ErrorCode;
import com.wowa.f1betting.repository.RaceRepository;
import com.wowa.f1betting.repository.UserRepository;
import com.wowa.f1betting.service.RaceService;
import com.wowa.f1betting.service.RoundFlowManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/race")
public class RoundController {

    private final RoundFlowManager flowManager;
    private final RaceService raceService;
    private final RaceRepository raceRepository;
    private final UserRepository userRepository;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Long>> startRace(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(
                userDetails.getUsername().replace("USER_ID:", "")
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Race race = raceService.createRaceForUser(user);
        raceService.startRace(race);

        return ResponseEntity.ok(Map.of("raceId", race.getId()));
    }


    @PostMapping("/next")
    public ResponseEntity<Race> startRound(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Long> request
    ) {
        Long userId = Long.parseLong(
                userDetails.getUsername().replace("USER_ID:", "")
        );

        Long raceId = request.get("raceId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new CustomException(ErrorCode.RACE_NOT_FOUND));

        if (race.getRaceStatus() != RaceStatus.RUNNING) {
            throw new CustomException(ErrorCode.RACE_NOT_RUNNING);
        }

        if (!race.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        int nextRound = race.getCurrentRound() + 1;
        race.setCurrentRound(nextRound);
        raceRepository.save(race);

        log.info("다음 라운드 진행! RaceID: {}, NextRound: {}", race.getId(), nextRound);

        flowManager.startRoundFlow(race.getId(), nextRound, userId);

        return ResponseEntity.ok(race);
    }
}