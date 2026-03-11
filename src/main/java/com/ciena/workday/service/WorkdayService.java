package com.ciena.workday.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ciena.workday.exception.InvalidDateException;
import com.ciena.workday.model.Holiday;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class WorkdayService {

    // Just add some changes in the feature branch 

    private final Set<LocalDate> holidays;

    public WorkdayService() {
        this.holidays = loadHolidays();
    }

    public LocalDate addBusinessDays(LocalDate startDate, int businessDays) {
        if (startDate == null) {
            throw new InvalidDateException("Start date cannot be null");
        }
        if (businessDays < 0) {
            throw new InvalidDateException("Business days cannot be negative: " + businessDays);
        }

        LocalDate current = startDate;
        int daysAdded = 0;

        while (daysAdded < businessDays) {
            current = current.plusDays(1);
            if (isBusinessDay(current)) {
                daysAdded++;
            }
        }

        return current;
    }

    private boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY &&
               day != DayOfWeek.SUNDAY &&
               !holidays.contains(date);
    }

    private Set<LocalDate> loadHolidays() {
        
        //try (InputStream is = getClass().getResourceAsStream("/holidays.json")) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("holidays.json")) {
            if (is == null) {
                System.out.println("holidays.json not found → no holidays loaded");
                return Collections.emptySet();
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            List<Holiday> holidayList = mapper.readValue(is, new TypeReference<>() {});
            return holidayList.stream()
                    .map(Holiday::date)
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            System.err.println("Failed to load holidays: " + e.getMessage());
            return Collections.emptySet();
        }
    }
}