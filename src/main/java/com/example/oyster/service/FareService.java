package com.example.oyster.service;

import com.example.oyster.model.Fare;
import com.example.oyster.model.Station;
import com.example.oyster.repository.FareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FareService {

    @Autowired
    private FareRepository fareRepository;

    public BigDecimal calculateFare(Station startStation, Station endStation) {
        int startZone = startStation.getZone();
        int endZone = endStation.getZone();

        boolean isPeak = isPeakHour(LocalDateTime.now());

        return fareRepository.findByStartZoneAndEndZoneAndIsPeak(startZone, endZone, isPeak).orElseThrow().getFare();
    }

    public BigDecimal getMaxFare(Station startStation) {
        return fareRepository.findByStartZoneAndEndZoneAndIsPeak(startStation.getZone(), 3, true)
                .orElseThrow().getFare();
    }

    private boolean isPeakHour(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int totalMinutes = hour * 60 + minute;
        //peak and off-peak
        int morningPeakStart = 6 * 60 + 30; // 6:30 AM in minutes
        int morningPeakEnd = 9 * 60 + 30;   // 9:30 AM in minutes
        int eveningPeakStart = 16 * 60;     // 4:00 PM in minutes
        int eveningPeakEnd = 19 * 60;       // 7:00 PM in minutes

        return (totalMinutes >= morningPeakStart && totalMinutes <= morningPeakEnd) ||
                (totalMinutes >= eveningPeakStart && totalMinutes <= eveningPeakEnd);
    }
}