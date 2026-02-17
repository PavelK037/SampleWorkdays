package com.ciena.workday.api;

import java.time.LocalDate;

public record WorkdayRequest(LocalDate startDate, int businessDays) {
}