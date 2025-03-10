package com.example.oyster.service;

import com.example.oyster.model.Fare;
import com.example.oyster.model.Station;
import com.example.oyster.repository.FareRepository;
import com.example.oyster.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FareServiceTest {

    @Autowired
    private FareService fareService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private FareRepository fareRepository;

    private Station station1;
    private Station station2;
    private Fare fare1;
    private Fare fare2;

    @BeforeEach
    void setUp() {
        station1 = new Station();
        station1.setName("Station 1");
        station1.setZone(1);
        stationRepository.save(station1);
        station2 = new Station();
        station2.setName("Station 2");
        station2.setZone(2);
        stationRepository.save(station2);

        fare1 = new Fare();
        fare1.setStartZone(1);
        fare1.setEndZone(2);
        fare1.setIsPeak(true);
        fare1.setFare(BigDecimal.valueOf(3.00));
        fareRepository.save(fare1);

        fare2 = new Fare();
        fare2.setStartZone(1);
        fare2.setEndZone(2);
        fare2.setIsPeak(false);
        fare2.setFare(BigDecimal.valueOf(2.50));
        fareRepository.save(fare2);
    }

    @Test
    void throwsExceptionIfNoFareForGivenZones() {
        Station station3 = new Station();
        station3.setName("Station 3");
        station3.setZone(3);
        stationRepository.save(station3);

        Station station4 = new Station();
        station4.setName("Station 4");
        station4.setZone(4);
        stationRepository.save(station4);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                fareService.calculateFare(station3, station4));
        assertEquals(String.format("Fare not defined for zones 3 to 4 (peak: %s)", fareService.isPeakHour(LocalDateTime.now())), e.getMessage());
    }

    @Test
    void getsMaxFareForStartZone() {
        BigDecimal maxFare = fareService.getMaxFare(station1);
        assertEquals(BigDecimal.valueOf(3.00), maxFare);
    }

    @Test
    void throwsExceptionIfNoFareForStartZone() {
        Station station5 = new Station();
        station5.setName("Station 5");
        station5.setZone(5);
        stationRepository.save(station5);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                fareService.getMaxFare(station5));
        assertEquals("No fare data available for start zone: 5", e.getMessage());
    }

}
