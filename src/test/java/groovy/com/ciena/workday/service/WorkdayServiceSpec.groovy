package com.ciena.workday.service

import com.ciena.workday.exception.InvalidDateException
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class WorkdayServiceSpec extends Specification {

    def service = new WorkdayService()

    @Unroll
    def "should add #businessDays business days from #start to #expected"() {
        expect:
        service.addBusinessDays(start, businessDays) == expected

        where:
        start                | businessDays | expected
        LocalDate.of(2023,10,5) | 0            | LocalDate.of(2023,10,5)
        LocalDate.of(2023,10,5) | 1            | LocalDate.of(2023,10,6)
        LocalDate.of(2023,10,6) | 1            | LocalDate.of(2023,10,9)
    }

    def "should throw on null start date"() {
        when:
        service.addBusinessDays(null, 5)

        then:
        thrown(InvalidDateException)
    }
}