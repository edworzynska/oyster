package com.example.oyster.repository;

import com.example.oyster.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByZone(int zone);
}
