package com.example.oyster.controller;

import com.example.oyster.model.Station;
import com.example.oyster.service.AuthenticationService;
import com.example.oyster.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/")
    public ResponseEntity<List<Station>> getAllByZone(@RequestParam(required = false) Integer zone) {
        List<Station> stations;
        if (zone != null) {
            stations = stationService.getStationsByZone(zone);
        } else {
            stations = stationService.getStations();
        }
        return ResponseEntity.ok(stations);
    }
    @PostMapping("/")
    public ResponseEntity<Station> postStation(@Valid @RequestBody Station station){
        Station newStation = stationService.addStation(station);
        return ResponseEntity.status(HttpStatus.CREATED).body(newStation);
    }
}
