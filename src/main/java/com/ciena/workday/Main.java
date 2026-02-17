package com.ciena.workday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.ciena.workday.service.WorkdayService;

public class Main {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar app.jar <start-date> <business-days>");
            System.err.println("Example: java -jar app.jar 2023-10-05 2");
            System.exit(1);
        }

        try {
            LocalDate start = LocalDate.parse(args[0], DATE_FORMAT);
            int n = Integer.parseInt(args[1]);

            WorkdayService service = new WorkdayService();
            LocalDate result = service.addBusinessDays(start, n);

            System.out.printf("Start date: %s%n", start);
            System.out.printf("After %d business days: %s%n", n, result);
            System.out.printf("Formatted: %s%n", result.format(DATE_FORMAT));

        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Use yyyy-MM-dd (e.g. 2023-10-05)");
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number of business days: " + args[1]);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}