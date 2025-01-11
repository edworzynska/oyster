package com.example.oyster.service;

import com.example.oyster.model.Station;
import com.example.oyster.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    private Station station1;
    private Station station2;

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
    }

    @Test
    void getStations() {
        var stations = stationService.getStations();
        assertEquals(2, stations.size());
    }

    @Test
    void addStation() {
        Station station3 = new Station();
        station3.setName("Station 3");
        station3.setZone(3);

        Station addedStation = stationService.addStation(station3);

        assertNotNull(addedStation.getId());
        assertEquals("Station 3", addedStation.getName());
    }

    @Test
    void getStationsByZone() {
        var stationsInZone1 = stationService.getStationsByZone(1);
        assertEquals(1, stationsInZone1.size());
        assertEquals("Station 1", stationsInZone1.get(0).getName());

        var stationsInZone2 = stationService.getStationsByZone(2);
        assertEquals(1, stationsInZone2.size());
        assertEquals("Station 2", stationsInZone2.get(0).getName());
    }

    @Test
    void getStationsReturnsEmptyWhenNoneExist() {
        stationRepository.deleteAll();

        var stations = stationService.getStations();
        assertTrue(stations.isEmpty());
    }
}
