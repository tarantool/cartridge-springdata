package org.springframework.data.tarantool.core.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

/**
 * JSR-310 specific {@link Converter} implementations for converting date and time.
 *
 * @author Alexey Kuzin
 */
public class TarantoolJsr310Converters {
    /**
     * Returns the converters to be registered
     *
     * @return
     */
    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(NumberToLocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToLongConverter.INSTANCE);
        converters.add(NumberToLocalDateConverter.INSTANCE);
        converters.add(LocalDateToLongConverter.INSTANCE);
        converters.add(NumberToLocalTimeConverter.INSTANCE);
        converters.add(LocalTimeToLongConverter.INSTANCE);
        converters.add(NumberToInstantConverter.INSTANCE);
        converters.add(InstantToLongConverter.INSTANCE);
        converters.add(ZoneIdToStringConverter.INSTANCE);
        converters.add(StringToZoneIdConverter.INSTANCE);
        converters.add(DurationToStringConverter.INSTANCE);
        converters.add(StringToDurationConverter.INSTANCE);
        converters.add(PeriodToStringConverter.INSTANCE);
        converters.add(StringToPeriodConverter.INSTANCE);
        return converters;
    }

    @ReadingConverter
    public enum NumberToLocalDateTimeConverter implements Converter<Number, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(Number source) {
            return source == null ? null
                    : ofInstant(DateConverters.SerializedObjectToDateConverter.INSTANCE.convert(source).toInstant(),
                    systemDefault());
        }
    }

    @WritingConverter
    public enum LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long> {

        INSTANCE;

        @Override
        public Long convert(LocalDateTime source) {
            return source == null ? null
                    : DateConverters.DateToLongConverter.INSTANCE.convert(Date.from(source.atZone(systemDefault()).toInstant()));
        }
    }

    @ReadingConverter
    public enum NumberToLocalDateConverter implements Converter<Number, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(Number source) {
            return source == null ? null
                    : ofInstant(ofEpochMilli(DateConverters.SerializedObjectToDateConverter.INSTANCE.convert(source).getTime()),
                    systemDefault()).toLocalDate();
        }
    }

    @WritingConverter
    public enum LocalDateToLongConverter implements Converter<LocalDate, Long> {

        INSTANCE;

        @Override
        public Long convert(LocalDate source) {
            return source == null ? null
                    : DateConverters.DateToLongConverter.INSTANCE
                    .convert(Date.from(source.atStartOfDay(systemDefault()).toInstant()));
        }
    }

    @ReadingConverter
    public enum NumberToLocalTimeConverter implements Converter<Number, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(Number source) {
            return source == null ? null
                    : ofInstant(ofEpochMilli(DateConverters.SerializedObjectToDateConverter.INSTANCE.convert(source).getTime()),
                    systemDefault()).toLocalTime();
        }
    }

    @WritingConverter
    public enum LocalTimeToLongConverter implements Converter<LocalTime, Long> {

        INSTANCE;

        @Override
        public Long convert(LocalTime source) {
            return source == null ? null
                    : DateConverters.DateToLongConverter.INSTANCE
                    .convert(Date.from(source.atDate(LocalDate.now()).atZone(systemDefault()).toInstant()));
        }
    }

    @ReadingConverter
    public enum NumberToInstantConverter implements Converter<Number, Instant> {

        INSTANCE;

        @Override
        public Instant convert(Number source) {
            return source == null ? null
                    : DateConverters.SerializedObjectToDateConverter.INSTANCE.convert(source).toInstant();
        }
    }

    @WritingConverter
    public enum InstantToLongConverter implements Converter<Instant, Long> {

        INSTANCE;

        @Override
        public Long convert(Instant source) {
            return source == null ? null
                    : DateConverters.DateToLongConverter.INSTANCE.convert(Date.from(source.atZone(systemDefault()).toInstant()));
        }
    }

    @WritingConverter
    public enum ZoneIdToStringConverter implements Converter<ZoneId, String> {

        INSTANCE;

        @Override
        public String convert(ZoneId source) {
            return source.toString();
        }
    }

    @ReadingConverter
    public enum StringToZoneIdConverter implements Converter<String, ZoneId> {

        INSTANCE;

        @Override
        public ZoneId convert(String source) {
            return ZoneId.of(source);
        }
    }

    @WritingConverter
    public enum DurationToStringConverter implements Converter<Duration, String> {

        INSTANCE;

        @Override
        public String convert(Duration duration) {
            return duration.toString();
        }
    }

    @ReadingConverter
    public enum StringToDurationConverter implements Converter<String, Duration> {

        INSTANCE;

        @Override
        public Duration convert(String s) {
            return Duration.parse(s);
        }
    }

    @WritingConverter
    public enum PeriodToStringConverter implements Converter<Period, String> {

        INSTANCE;

        @Override
        public String convert(Period period) {
            return period.toString();
        }
    }

    @ReadingConverter
    public enum StringToPeriodConverter implements Converter<String, Period> {

        INSTANCE;

        @Override
        public Period convert(String s) {
            return Period.parse(s);
        }
    }
}
