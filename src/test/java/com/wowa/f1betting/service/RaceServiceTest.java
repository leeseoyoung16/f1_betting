package com.wowa.f1betting.service;

import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceStatus;
import com.wowa.f1betting.domain.user.User;
import com.wowa.f1betting.repository.RaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class RaceServiceTest {

    RaceRepository raceRepository = mock(RaceRepository.class);

    RaceService service = new RaceService(raceRepository);

    private User createUser(Long id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private Race createRace(Long id, RaceStatus status, User user) {
        Race r = new Race();
        r.setId(id);
        r.setRaceStatus(status);
        r.setUser(user);
        return r;
    }

    @Test
    @DisplayName("기존 RUNNING 레이스를 정리하고 새로운 READY 레이스를 생성")
    void createRaceForUser_success() {
        // given
        User user = createUser(10L);

        Race active1 = createRace(1L, RaceStatus.RUNNING, user);

        given(raceRepository.findAllByUserAndRaceStatus(user, RaceStatus.RUNNING))
                .willReturn(List.of(active1));

        // when
        Race result = service.createRaceForUser(user);

        // then
        assertThat(active1.getRaceStatus()).isEqualTo(RaceStatus.FINISHED);

        assertThat(result.getRaceStatus()).isEqualTo(RaceStatus.READY);
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("RUNNING 레이스를 모두 FINISHED로 변경")
    void cleanupActiveRaces_success() {
        // given
        User user = createUser(10L);

        Race r1 = createRace(1L, RaceStatus.RUNNING, user);

        given(raceRepository.findAllByUserAndRaceStatus(user, RaceStatus.RUNNING))
                .willReturn(List.of(r1));

        // when
        service.cleanupActiveRaces(user);

        // then
        assertThat(r1.getRaceStatus()).isEqualTo(RaceStatus.FINISHED);
    }

    @Test
    @DisplayName("레이스를 RUNNING 상태로 변경하고 currentRound=0으로 초기화")
    void startRace_success() {
        // given
        User user = createUser(10L);
        Race race = createRace(1L, RaceStatus.READY, user);

        // when
        service.startRace(race);

        // then
        assertThat(race.getRaceStatus()).isEqualTo(RaceStatus.RUNNING);
        assertThat(race.getCurrentRound()).isEqualTo(0);

        verify(raceRepository).save(race);
    }

    @Test
    @DisplayName("레이스 상태를 FINISHED로 변경")
    void finishRace_success() {
        // given
        User user = createUser(10L);
        Race race = createRace(1L, RaceStatus.RUNNING, user);

        // when
        service.finishRace(race);

        // then
        assertThat(race.getRaceStatus()).isEqualTo(RaceStatus.FINISHED);
        verify(raceRepository).save(race);
    }
}
