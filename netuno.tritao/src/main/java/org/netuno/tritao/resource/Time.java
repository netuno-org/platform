/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Hili;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.Instant;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Time - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "time")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Time",
                introduction = "Gestão de dados temporal baseada no **java.time**.",
                howToUse = { }
        )
})
public class Time extends ResourceBase {

    public Time(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public SimpleDateFormat simpleDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public GregorianCalendar gregorianCalendar() {
        return new java.util.GregorianCalendar();
    }

    public GregorianCalendar gregorianCalendar(TimeZone zone) {
        return new GregorianCalendar(zone);
    }

    public GregorianCalendar gregorianCalendar(Locale aLocale) {
        return new GregorianCalendar(aLocale);
    }

    public GregorianCalendar gregorianCalendar(TimeZone zone, Locale aLocale) {
        return new GregorianCalendar(zone, aLocale);
    }

    public GregorianCalendar gregorianCalendar(int year, int month, int dayOfMonth) {
        return new GregorianCalendar(year, month, dayOfMonth);
    }

    public GregorianCalendar gregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
    }

    public GregorianCalendar gregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public int gregorianCalendarConstant(String name) throws ResourceException {
        name = name.toLowerCase().replace('-', '_');
        try {
            int value = (Integer)GregorianCalendar
                    .class
                    .getDeclaredField(name.toUpperCase())
                    .get(Integer.class);
            return value;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new ResourceException("time.gregorianCalendarConstant(" + name + ")", e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Converte o objeeto passado para TemporalAcessor.",
                    howToUse = { })
    }, parameters = {}, returns = {})
    public TemporalAccessor castToTemporalAcessor(Object o) {
        return (TemporalAccessor)o;
    }
    
    public Temporal castToTemporal(Object o) {
        return (Temporal)o;
    }
    
    public ChronoLocalDate castToChronoLocalDate(Object o) {
        return (ChronoLocalDate)o;
    }

    public long currentMilis() {
        return System.currentTimeMillis();
    }

    public Date date() {
        return new Date();
    }

    public Date dateFrom(Instant instant) {
        return Date.from(instant);
    }

    //    <    java.time    >

    public Clock clock() {
        return Clock.systemDefaultZone();
    }
    
    public Clock clockFixed(Instant fixedInstant, ZoneId zone) {
        return Clock.fixed(fixedInstant, zone);
    }
    
    public Clock clockOffset(Clock baseClock, Duration offsetDuration) {
        return Clock.offset(baseClock, offsetDuration);
    }
    
    public Clock clockSystem(ZoneId zone) {
        return Clock.system(zone);
    }
    
    public Clock clockSystemUTC() {
        return Clock.systemUTC();
    }
    
    public Clock clockTick(Clock baseClock, Duration tickDuration) {
        return Clock.tick(baseClock, tickDuration);
    }
    
    public Clock clockTickMinutes(ZoneId zone) {
        return Clock.tickMinutes(zone);
    }
    
    public Clock clockTickSeconds(ZoneId zone) {
        return Clock.tickSeconds(zone);
    }

    public Duration duration() {
        return Duration.ZERO;
    }

    public Duration durationBetween(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    public Duration durationFrom(TemporalAmount temporalAmount) {
        return Duration.from(temporalAmount);
    }

    public Duration duration(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            Duration duration = (Duration)Duration
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(Duration.class);
            return duration;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public Duration durationOf(long amount, TemporalUnit unit) {
        return Duration.of(amount, unit);
    }

    public Duration durationOfDays(long days) {
        return Duration.ofDays(days);
    }

    public Duration durationOfHours(long hours) {
        return Duration.ofHours(hours);
    }

    public Duration durationOfMillis(long millis) {
        return Duration.ofMillis(millis);
    }

    public Duration durationOfMinutes(long minutes) {
        return Duration.ofMinutes(minutes);
    }

    public Duration durationOfNanos(long nanos) {
        return Duration.ofNanos(nanos);
    }

    public Duration durationOfSeconds(long seconds) {
        return Duration.ofSeconds(seconds);
    }

    public Duration durationOfSeconds(long seconds, long nanoAdjustment) {
        return Duration.ofSeconds(seconds, nanoAdjustment);
    }

    public Instant instant() {
        return Instant.now();
    }

    public Instant instant(Clock clock) {
        return Instant.now(clock);
    }

    public Instant instantFrom(TemporalAccessor temporalAccessor) {
        return Instant.from(temporalAccessor);
    }

    public Instant instantParse(CharSequence text) {
        return Instant.parse(text);
    }

    public Instant instant(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            Instant instant = (Instant)Instant
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(Instant.class);
            return instant;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public Instant instantOfEpochMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli);
    }

    public Instant instantOfEpochSecond(long epochsecond) {
        return Instant.ofEpochSecond(epochsecond);
    }

    public Instant instantOfEpochSecond(long epochsecond, long nanoAdjustment) {
        return Instant.ofEpochSecond(epochsecond, nanoAdjustment);
    }

    public LocalDate localDate() {
        return LocalDate.now();
    }

    public LocalDate localDate(Clock clock) {
        return LocalDate.now(clock);
    }

    public LocalDate localDate(ZoneId zone) {
        return LocalDate.now(zone);
    }

    public LocalDate localDateFrom(TemporalAccessor temporalAccessor) {
        return LocalDate.from(temporalAccessor);
    }

    public LocalDate localDateParse(CharSequence text) {
        return LocalDate.parse(text);
    }

    public LocalDate localDateParse(CharSequence text, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(text, dateTimeFormatter);
    }

    public LocalDate localDate(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            LocalDate localDate = (LocalDate)LocalDate
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(LocalDate.class);
            return localDate;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public LocalDate localDateOf(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public LocalDate localDateOf(int year, Month month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public LocalDate localDateOfEpochDay(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    public LocalDate localDateOfYearDay(int year, int dayOfYear) {
        return LocalDate.ofYearDay(year, dayOfYear);
    }

    public LocalDateTime localDateTime() {
        return LocalDateTime.now();
    }

    public LocalDateTime localDateTime(Clock clock) {
        return LocalDateTime.now(clock);
    }

    public LocalDateTime localDateTime(ZoneId zone) {
        return LocalDateTime.now(zone);
    }

    public LocalDateTime localDateTimeFrom(TemporalAccessor temporalAccessor) {
        return LocalDateTime.from(temporalAccessor);
    }

    public LocalDateTime localDateTimeParse(CharSequence text) {
        return LocalDateTime.parse(text);
    }

    public LocalDateTime localDateTimeParse(CharSequence text, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(text, dateTimeFormatter);
    }

    public LocalDateTime localDateTime(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            LocalDateTime localDateTime = (LocalDateTime)LocalDateTime
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(LocalDateTime.class);
            return localDateTime;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public LocalDateTime localDateTimeOf(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }

    public LocalDateTime localDateTimeOf(int year, int month, int dayOfMonth, int hour, int minute) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public LocalDateTime localDateTimeOf(int year, Month month, int dayOfMonth, int hour, int minute) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    public LocalDateTime localDateTimeOf(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }

    public LocalDateTime localDateTimeOf(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }

    public LocalDateTime localDateTimeOf(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }

    public LocalDateTime localDateTimeOf(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }

    public LocalDateTime localDateTimeOfEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        return LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset);
    }

    public LocalDateTime localDateTimeOfInstant(Instant instant, ZoneId zone) {
        return LocalDateTime.ofInstant(instant, zone);
    }

    public LocalTime localTime() {
        return LocalTime.now();
    }

    public LocalTime localTime(Clock clock) {
        return LocalTime.now(clock);
    }

    public LocalTime localTime(ZoneId zone) {
        return LocalTime.now(zone);
    }

    public LocalTime localTimeFrom(TemporalAccessor temporalAccessor) {
        return LocalTime.from(temporalAccessor);
    }

    public LocalTime localTimeParse(CharSequence text) {
        return LocalTime.parse(text);
    }

    public LocalTime localTimeParse(CharSequence text, DateTimeFormatter dateTimeFormatter) {
        return LocalTime.parse(text, dateTimeFormatter);
    }

    public LocalTime localTime(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            LocalTime localTime = (LocalTime)LocalTime
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(LocalTime.class);
            return localTime;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public LocalTime localTimeOf(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    public LocalTime localTimeOf(int hour, int minute, int second) {
        return LocalTime.of(hour, minute, second);
    }

    public LocalTime localTimeOf(int hour, int minute, int second, int nanoOfSecond) {
        return LocalTime.of(hour, minute, second, nanoOfSecond);
    }

    public LocalTime localTimeOfNanoOfDay(long nanoOfDay) {
        return LocalTime.ofNanoOfDay(nanoOfDay);
    }

    public LocalTime localTimeOfSecondOfDay(long secondOfDay) {
        return LocalTime.ofSecondOfDay(secondOfDay);
    }

    public MonthDay monthDay() {
        return MonthDay.now();
    }

    public MonthDay monthDay(Clock clock) {
        return MonthDay.now(clock);
    }

    public MonthDay monthDay(ZoneId zone) {
        return MonthDay.now(zone);
    }

    public MonthDay monthDayFrom(TemporalAccessor temporalAccessor) {
        return MonthDay.from(temporalAccessor);
    }

    public MonthDay monthDayOf(int month, int dayOfMonth) {
        return MonthDay.of(month, dayOfMonth);
    }

    public MonthDay monthDayOf(Month month, int dayOfMonth) {
        return MonthDay.of(month, dayOfMonth);
    }

    public OffsetDateTime offsetDateTime() {
        return OffsetDateTime.now();
    }

    public OffsetDateTime offsetDateTimeFrom(TemporalAccessor temporalAccessor) {
        return OffsetDateTime.from(temporalAccessor);
    }

    public OffsetDateTime offsetDateTime(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            OffsetDateTime offsetDateTime = (OffsetDateTime)OffsetDateTime
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(OffsetDateTime.class);
            return offsetDateTime;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public OffsetDateTime offsetDateTimeOf(LocalDateTime dateTime, ZoneOffset offset) {
        return OffsetDateTime.of(dateTime, offset);
    }

    public OffsetDateTime offsetDateTimeOf(LocalDate date, LocalTime time, ZoneOffset offset) {
        return OffsetDateTime.of(date, time, offset);
    }

    public OffsetDateTime offsetDateTimeOf(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        return OffsetDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, offset);
    }

    public OffsetDateTime offsetDateTimeOfInstant(Instant instant, ZoneOffset offset) {
        return OffsetDateTime.ofInstant(instant, offset);
    }

    public OffsetTime offsetTime() {
        return OffsetTime.now();
    }

    public OffsetTime offsetTime(Clock clock) {
        return OffsetTime.now(clock);
    }

    public OffsetTime offsetTime(ZoneId zone) {
        return OffsetTime.now(zone);
    }

    public OffsetTime offsetTimeFrom(TemporalAccessor temporalAccessor) {
        return OffsetTime.from(temporalAccessor);
    }

    public OffsetTime offsetTime(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            OffsetTime offsetTime = (OffsetTime)OffsetTime
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(OffsetTime.class);
            return offsetTime;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public OffsetTime offsetTimeOf(LocalTime time, ZoneOffset offset) {
        return OffsetTime.of(time, offset);
    }

    public OffsetTime offsetTimeOf(int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        return OffsetTime.of(hour, minute, second, nanoOfSecond, offset);
    }

    public OffsetTime offsetTimeOfInstant(Instant instant, ZoneId zone) {
        return OffsetTime.ofInstant(instant, zone);
    }

    public Period period() {
        return Period.ZERO;
    }

    public Period periodFrom(TemporalAmount temporalAmount) {
        return Period.from(temporalAmount);
    }

    public Period period(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            Period period = (Period)Period
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(Period.class);
            return period;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public Period periodOf(int years, int months, int days) {
        return Period.of(years, months, days);
    }

    public Period periodOfDays(int days) {
        return Period.ofDays(days);
    }

    public Period periodOfMonths(int months) {
        return Period.ofMonths(months);
    }

    public Period periodOfWeeks(int weeks) {
        return Period.ofWeeks(weeks);
    }

    public Period periodOfYears(int years) {
        return Period.ofYears(years);
    }

    public Year year() {
        return Year.now();
    }

    public Year year(Clock clock) {
        return Year.now(clock);
    }

    public Year year(ZoneId zone) {
        return Year.now(zone);
    }

    public Year yearFrom(TemporalAccessor temporalAccessor) {
        return Year.from(temporalAccessor);
    }

    public Year year(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            Year year = (Year)Year
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(Year.class);
            return year;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }
    
    public Year yearOf(int isoYear) {
    	return Year.of(isoYear);
    }

    public YearMonth yearMonth() {
        return YearMonth.now();
    }

    public YearMonth yearMonth(Clock clock) {
        return YearMonth.now(clock);
    }

    public YearMonth yearMonth(ZoneId zone) {
        return YearMonth.now(zone);
    }

    public YearMonth yearMonthFrom(TemporalAccessor temporalAccessor) {
        return YearMonth.from(temporalAccessor);
    }

    public YearMonth yearMonthOf(int year, int month) {
        return YearMonth.of(year, month);
    }

    public YearMonth yearMonthOf(int year, Month month) {
        return YearMonth.of(year, month);
    }

    public Month month(String type) {
    	type = type.toLowerCase().replace('-', '_');
        try {
        	Month month = (Month)Month
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(Month.class);
            return month;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }
    
    public Month monthOf(int month) {
    	return Month.of(month);
    }
    
    public Month monthValueOf(String name) {
    	return Month.valueOf(name);
    }

    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now();
    }

    public ZonedDateTime zonedDateTime(Clock clock) {
        return ZonedDateTime.now(clock);
    }

    public ZonedDateTime zonedDateTime(ZoneId zone) {
        return ZonedDateTime.now(zone);
    }

    public ZonedDateTime zonedDateTimeFrom(TemporalAccessor temporalAccessor) {
        return ZonedDateTime.from(temporalAccessor);
    }

    public ZonedDateTime zonedDateTimeParse(CharSequence text) {
        return ZonedDateTime.parse(text);
    }

    public ZonedDateTime zonedDateTimeParse(CharSequence text, DateTimeFormatter dateTimeFormatter) {
        return ZonedDateTime.parse(text, dateTimeFormatter);
    }

    public ZonedDateTime zonedDateTime(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            ZonedDateTime zonedDateTime = (ZonedDateTime)OffsetDateTime
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(OffsetDateTime.class);
            return zonedDateTime;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public ZonedDateTime zonedDateTimeOf(LocalDateTime localDateTime, ZoneId zone) {
        return ZonedDateTime.of(localDateTime, zone);
    }

    public ZonedDateTime zonedDateTimeOf(LocalDate date, LocalTime time, ZoneId zone) {
        return ZonedDateTime.of(date, time, zone);
    }

    public ZonedDateTime zonedDateTimeOf(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zone) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone);
    }

    public ZonedDateTime zonedDateTimeOfInstant(Instant instant, ZoneId zone) {
        return ZonedDateTime.ofInstant(instant, zone);
    }

    public ZonedDateTime zonedDateTimeOfInstant(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        return ZonedDateTime.ofInstant(localDateTime, offset, zone);
    }

    public ZonedDateTime zonedDateTimeOfLocal(LocalDateTime localDateTime, ZoneId zone, ZoneOffset preferredOffset) {
        return ZonedDateTime.ofLocal(localDateTime, zone, preferredOffset);
    }

    public ZonedDateTime zonedDateTimeOfLocal(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        return ZonedDateTime.ofStrict(localDateTime, offset, zone);
    }

    public ZoneId zoneId() {
        return ZoneId.systemDefault();
    }

    public ZoneId zoneIdFrom(TemporalAccessor temporalAccessor) {
        return ZoneId.from(temporalAccessor);
    }

    public ZoneId zoneIdOf(String zoneId) {
        return ZoneId.of(zoneId);
    }

    public ZoneId zoneIdOf(String zoneId, Map<String, String> aliasMap) {
        return ZoneId.of(zoneId, aliasMap);
    }

    public ZoneId zoneIdOfOffset(String prefix, ZoneOffset offset) {
        return ZoneId.ofOffset(prefix, offset);
    }

    public ZoneOffset zoneOffset() {
        return ZoneOffset.UTC;
    }

    public ZoneOffset zoneOffsetFrom(TemporalAccessor temporalAccessor) {
        return ZoneOffset.from(temporalAccessor);
    }

    public ZoneOffset zoneOffset(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            ZoneOffset zoneOffset = (ZoneOffset)ZoneOffset
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(ZoneOffset.class);
            return zoneOffset;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public ZoneOffset zoneOffsetOf(String offsetId) {
        return ZoneOffset.of(offsetId);
    }

    public ZoneOffset zoneOffsetOfHours(int hours) {
        return ZoneOffset.ofHours(hours);
    }

    public ZoneOffset zoneOffsetOfHoursMinutes(int hours, int minutes) {
        return ZoneOffset.ofHoursMinutes(hours, minutes);
    }

    public ZoneOffset zoneOffsetOfHoursMinutesSeconds(int hours, int minutes, int seconds) {
        return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);
    }

    public ZoneOffset zoneOffsetOfTotalSeconds(int totalSeconds) {
        return ZoneOffset.ofTotalSeconds(totalSeconds);
    }

    public ZoneId zoneOffsetOf(String zoneId, Map<String, String> aliasMap) {
        return ZoneOffset.of(zoneId, aliasMap);
    }

    public ZoneId zoneOffsetOfOffset(String prefix, ZoneOffset offset) {
        return ZoneOffset.ofOffset(prefix, offset);
    }

    //    <    /  java.time    >

    //    <    java.time.format    >

    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.BASIC_ISO_DATE;
    }

    public DateTimeFormatter dateTimeFormatter(String pattern) {
        String type = pattern.toLowerCase().replace('-', '_');
        try {
            DateTimeFormatter dateTimeFormatter = (DateTimeFormatter)DateTimeFormatter
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(DateTimeFormatter.class);
            return dateTimeFormatter;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return DateTimeFormatter.ofPattern(pattern);
        }
    }

    public DateTimeFormatter dateTimeFormatter(String type, Locale locale) {
        return DateTimeFormatter.ofPattern(type, locale);
    }

    public DateTimeFormatterBuilder dateTimeFormatterBuilder() {
        return new DateTimeFormatterBuilder();
    }

    public DecimalStyle decimalStyle() {
        return DecimalStyle.STANDARD;
    }

    public DecimalStyle decimalStyle(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            DecimalStyle decimalStyle = (DecimalStyle)DecimalStyle
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(DecimalStyle.class);
            return decimalStyle;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    //    <    /   java.time.format    >

    //    <    java.time.temporal    >

    public ChronoField chronoField(String field) {
        String type = field.toLowerCase().replace('-', '_');
        try {
            ChronoField chronoField = (ChronoField)ChronoField
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(ChronoField.class);
            return chronoField;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return ChronoField.valueOf(field);
        }
    }

    public ChronoUnit chronoUnit(String field) {
        String type = field.toLowerCase().replace('-', '_');
        try {
            ChronoUnit chronoUnit = (ChronoUnit)ChronoUnit
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(ChronoUnit.class);
            return chronoUnit;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return ChronoUnit.valueOf(field);
        }
    }

    public IsoFields isoFields(String field) {
        String type = field.toLowerCase().replace('-', '_');
        try {
            IsoFields isoFields = (IsoFields)IsoFields
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(IsoFields.class);
            return isoFields;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public JulianFields julianFields(String field) {
        String type = field.toLowerCase().replace('-', '_');
        try {
            JulianFields julianFields = (JulianFields)JulianFields
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(JulianFields.class);
            return julianFields;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    public ValueRange valueRangeOf(long min, long max) {
        return ValueRange.of(min, max);
    }

    public ValueRange valueRangeOf(long min, long maxSmallest, long maxLargest) {
        return ValueRange.of(min, maxSmallest, maxLargest);
    }

    public ValueRange valueRangeOf(long minSmallest, long minLargest, long maxSmallest, long maxLargest) {
        return ValueRange.of(minSmallest, minLargest, maxSmallest, maxLargest);
    }

    //    <    /   java.time.temporal    >

    //    <    java.util.concurrent    >

    public TimeUnit unit(String field) {
        String type = field.toLowerCase().replace('-', '_');
        try {
            TimeUnit timeUnit = (TimeUnit)TimeUnit
                    .class
                    .getDeclaredField(type.toUpperCase())
                    .get(TimeUnit.class);
            return timeUnit;
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    //    <    /   java.util.concurrent    >
}
