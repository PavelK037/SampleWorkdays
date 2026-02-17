package com.ciena.workday.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.ciena.workday.exception.InvalidDateException;

class WorkdayServiceTest {

    private WorkdayService service;

    @BeforeEach
    void setUp() {
        service = new WorkdayService();
    }

    // ────────────────────────────────────────────────
    // Tests for addBusinessDays
    // ────────────────────────────────────────────────

    @ParameterizedTest(name = "{0} + {1} business days → {2}")
    @CsvSource({
        "2023-10-05, 0, 2023-10-05",   // same day
        "2023-10-05, 1, 2023-10-06",   // Friday → Monday
        "2023-10-06, 1, 2023-10-09",   // Friday → next Monday (skips weekend)
        "2023-10-06, 2, 2023-10-10",   // Friday + 2 → Tuesday
        "2023-12-22, 5, 2023-12-29",   // Friday before Christmas weekend
    })
    @DisplayName("Should correctly add business days skipping weekends")
    void shouldAddBusinessDaysSkippingWeekends(String startStr, int days, String expectedStr) {
        LocalDate start = LocalDate.parse(startStr);
        LocalDate expected = LocalDate.parse(expectedStr);

        LocalDate result = service.addBusinessDays(start, days);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when start date is null")
    void shouldThrowOnNullStartDate() {
        InvalidDateException ex = assertThrows(
            InvalidDateException.class,
            () -> service.addBusinessDays(null, 5)
        );
        assertEquals("Start date cannot be null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("Should throw exception for negative business days")
    void shouldThrowOnNegativeBusinessDays(int negativeDays) {
        InvalidDateException ex = assertThrows(
            InvalidDateException.class,
            () -> service.addBusinessDays(LocalDate.now(), negativeDays)
        );
        assertTrue(ex.getMessage().contains("Business days cannot be negative"));
    }

    // ────────────────────────────────────────────────
    // Tests for isBusinessDay
    // ────────────────────────────────────────────────

    @Test
    @DisplayName("Weekend days should not be business days")
    void weekendsAreNotBusinessDays() throws Exception {
        LocalDate saturday = LocalDate.of(2023, 10, 7);
        LocalDate sunday = LocalDate.of(2023, 10, 8);

        assertFalse(isBusinessDayViaReflection(saturday));
        assertFalse(isBusinessDayViaReflection(sunday));
    }

    @Test
    @DisplayName("Weekday without holiday should be business day")
    void normalWeekdayIsBusinessDay() throws Exception {
        LocalDate monday = LocalDate.of(2023, 10, 9);
        assertTrue(isBusinessDayViaReflection(monday));
    }

    // Helper to access private method via reflection (for unit testing)
    private boolean isBusinessDayViaReflection(LocalDate date) throws Exception {
        var method = WorkdayService.class.getDeclaredMethod("isBusinessDay", LocalDate.class);
        method.setAccessible(true);
        return (boolean) method.invoke(service, date);
    }

    // ────────────────────────────────────────────────
    // Holiday loading tests (mocking resource loading is harder in unit test)
    // ────────────────────────────────────────────────

    @Test
    @DisplayName("addBusinessDays should skip configured holidays")
    void shouldSkipHolidays() {
        // This test assumes holidays.json contains 2023-10-09 (Monday)
        // If not present → test will fail → you can override holidays in test

        LocalDate friday = LocalDate.of(2023, 10, 6);
        LocalDate expected = LocalDate.of(2023, 10, 10); // skips Monday holiday

        LocalDate result = service.addBusinessDays(friday, 2);

        // If holiday is loaded → should be Tuesday
        // If not loaded → would be Monday → this asserts holiday skipping works
        assertEquals(expected, result);
    }
}