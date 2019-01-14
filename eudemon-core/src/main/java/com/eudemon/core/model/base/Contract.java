package com.eudemon.core.model.base;

import com.eudemon.core.model.holidays.CronTabExpression;
import com.eudemon.core.model.holidays.HolidayCalendar;
import com.eudemon.core.model.holidays.HolidayCalendarType;

import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@MappedSuperclass
public class Contract<E,F,G> extends AbstractAuditingEntity {
    String coderef;
    E party;
    F counterparty;
    List<G> projects;
    String name ;
    Instant startDate;
    Status status;
    CronTabExpression frequency;
    Instant endDate;
    Currency basecurrency;
   TimeZone timeZone;
    List<HolidayCalendar> holidayCalendars;

    public Currency getBasecurrency() {
        return basecurrency;
    }

    public void setBasecurrency(Currency basecurrency) {
        this.basecurrency = basecurrency;
    }

    public String getCoderef() {
        return coderef;
    }

    public void setCoderef(String coderef) {
        this.coderef = coderef;
    }

    Map<HolidayCalendarType,Group<HolidayCalendar>> holidays;

    public Map<HolidayCalendarType, Group<HolidayCalendar>> getHolidays() {
        return holidays;
    }

    public void setHolidays(Map<HolidayCalendarType, Group<HolidayCalendar>> holidays) {
        this.holidays = holidays;
    }


    public E getParty() {
        return party;
    }

    public void setParty(E party) {
        this.party = party;
    }

    public F getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(F counterparty) {
        this.counterparty = counterparty;
    }

    public List<G> getProjects() {
        return projects;
    }

    public void setProjects(List<G> projects) {
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CronTabExpression getFrequency() {
        return frequency;
    }

    public void setFrequency(CronTabExpression frequency) {
        this.frequency = frequency;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }
}
