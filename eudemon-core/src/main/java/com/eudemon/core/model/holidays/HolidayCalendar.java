package com.eudemon.core.model.holidays;

import com.eudemon.core.model.base.AbstractAuditingEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Entity
@Table(name = "CORE_HOLIDAYS")
public class HolidayCalendar extends AbstractAuditingEntity {
    HolidayCalendarType type;
    String name;

    TimeZone timeZone;

    List<DayOfWeek> weekend;

    List<CronTabExpression> recursiveHolidays;

    List<Date>   holidays;

    public HolidayCalendarType getType() {
        return type;
    }

    public void setType(HolidayCalendarType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public List<DayOfWeek> getWeekend() {
        return weekend;
    }

    public void setWeekend(List<DayOfWeek> weekend) {
        this.weekend = weekend;
    }

    public List<CronTabExpression> getRecursiveHolidays() {
        return recursiveHolidays;
    }

    public void setRecursiveHolidays(List<CronTabExpression> recursiveHolidays) {
        this.recursiveHolidays = recursiveHolidays;
    }

    public List<Date> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Date> holidays) {
        this.holidays = holidays;
    }
}
