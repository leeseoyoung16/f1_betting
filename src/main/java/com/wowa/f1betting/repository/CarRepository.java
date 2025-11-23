package com.wowa.f1betting.repository;

import com.wowa.f1betting.domain.race.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
