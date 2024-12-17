package com.example.oyster.service;

import com.example.oyster.model.Station;
import com.example.oyster.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    public List<Station> getStations(){
        return stationRepository.findAll();
    }
    public Station addStation(Station station){
        stationRepository.save(station);
        return station;
    }
    public List<Station> getStationsByZone(Integer zone){
        return stationRepository.findByZone(zone);
    }
}
