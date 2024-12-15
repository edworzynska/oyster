package com.example.oyster.repository;

import com.example.oyster.model.DailyCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DailyCapRepository extends JpaRepository<DailyCap, Long> {
    Optional<DailyCap> findByStartZoneAndEndZone(int startZone, int endZone);}