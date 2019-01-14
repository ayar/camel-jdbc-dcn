package com.eudemon.core.model.base;

import com.eudemon.core.model.holidays.HolidayCalendar;

import javax.persistence.*;
import java.util.List;
import java.util.TimeZone;


@Entity
@Table(name = "CORE_CITY")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    String name;
    String code;
    Country country;
    TimeZone timezone;

}
