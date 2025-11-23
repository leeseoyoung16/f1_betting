package com.wowa.f1betting.game.selector;

import com.wowa.f1betting.domain.race.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CarSelectorTest {

    CarSelector selector = new CarSelector();

    private Car car(long id) {
        Car c = new Car();
        c.setId(id);
        return c;
    }

    @Test
    @DisplayName("랜덤 그룹 선택 시 지정된 개수만큼 차량을 반환")
    void randomGroupReturnsCorrectSize() {
        List<Car> cars = new ArrayList<>(List.of(
                car(1L), car(2L), car(3L), car(4L), car(5L)
        ));

        List<Car> result = selector.randomGroup(cars, 3);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("랜덤 그룹 선택 결과는 원본 리스트의 부분집합")
    void randomGroupSubsetOfOriginal() {
        List<Car> cars = new ArrayList<>(List.of(
                car(1L), car(2L), car(3L), car(4L), car(5L)
        ));

        List<Car> result = selector.randomGroup(cars, 2);

        assertThat(cars).containsAll(result);
    }
}
