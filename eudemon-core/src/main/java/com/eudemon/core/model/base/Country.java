package com.eudemon.core.model.base;

import com.eudemon.core.model.holidays.HolidayCalendar;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CORE_COUNTRY")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    String name;
    String iso2code;
    String iso3code;
    Region region;
    City capital;
    List<Currency> currencies;
    List<City> cities;
    List<HolidayCalendar> holidays;
}
