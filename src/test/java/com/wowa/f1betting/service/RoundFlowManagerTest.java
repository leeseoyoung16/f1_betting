package com.wowa.f1betting.service;

import com.wowa.f1betting.config.RaceProgressPublisher;
import com.wowa.f1betting.domain.race.Car;
import com.wowa.f1betting.domain.race.Race;
import com.wowa.f1betting.domain.race.RaceRound;
import com.wowa.f1betting.dto.bet.CarOddsDto;
import com.wowa.f1betting.dto.bet.CarOddsResponseDto;
import com.wowa.f1betting.game.state.RoundStateMachine;
import com.wowa.f1betting.game.state.RoundEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoundFlowManagerTest {

    @Mock RoundSetupService setupService;
    @Mock RoundStateMachine stateMachine;
    @Mock ObjectFactory<RoundStateMachine> stateMachineFactory;

    @InjectMocks RoundFlowManager flowManager;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(flowManager, "stateMachineFactory", stateMachineFactory);
    }

    @Test
    @DisplayName("라운드 흐름이 정상적으로 실행될 때 단계 호출이 순서대로 발생")
    void flowRunsCorrectly() {

        RaceRound round = new RaceRound();
        round.setId(10L);

        when(setupService.prepare(99L, 3)).thenReturn(round);
        when(stateMachineFactory.getObject()).thenReturn(stateMachine);

        when(setupService.calculateCarMultipliers(10L))
                .thenReturn(List.of(new CarOddsDto(1L, "carA", 2.0)));

        flowManager.startRoundFlow(99L, 3, 777L);

        verify(stateMachine).handle(RoundEvent.START_ROUND);
        verify(stateMachine).handle(RoundEvent.OPEN_BETTING);
        verify(stateMachine).handle(RoundEvent.CLOSE_BETTING);
        verify(stateMachine).handle(RoundEvent.DO_PAYOUT);
    }
}
