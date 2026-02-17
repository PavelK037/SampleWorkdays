package com.ciena.workday.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record Holiday(LocalDate date) {

    @JsonCreator
    public Holiday(@JsonProperty("date") String dateStr) {
        this(LocalDate.parse(dateStr));
    }
}