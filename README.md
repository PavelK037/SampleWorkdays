# GetWorkdaysApp

Just Example of the  README file -  should be correct by the app maintained .... 

Console application that calculates a future date after a given number of **business days** (workdays), skipping weekends and optionally holidays.

## Features

- Skips Saturday & Sunday
- Optional holiday support (loaded from `src/main/resources/holidays.json`)
- Input validation
- Clean, testable code (JUnit 5 + Spock)

## Build & Run

```bash
# Build project
./gradlew build

# Run (replace date & n with your values)
./gradlew run --args="2023-10-05 2"

# With holidays
# Just make sure holidays.json is present and valid