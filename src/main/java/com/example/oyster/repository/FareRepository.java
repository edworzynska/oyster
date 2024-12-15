package com.example.oyster.repository;

import com.example.oyster.model.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface FareRepository extends JpaRepository<Fare, Long> {

    Fare findByStartZoneAndEndZoneAndIsPeak(Integer startZone, Integer endZone, Boolean isPeak);
}