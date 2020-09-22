package org.springframework.data.tarantool.core.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.time.ZoneId.systemDefault;

/**
 * @author Alexey Kuzin
 */
public class DateConverters {
    /**
     * Returns all converters by this class that can be registered.
     *
     * @return the list of converters to register.
     */
    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

        boolean useISOStringConverterForDate = Boolean
                .parseBoolean(System.getProperty("org.springframework.data.couchbase.useISOStringConverterForDate", "false"));

        if (useISOStringConverterForDate) {
            converters.add(DateToStringConverter.INSTANCE);
        } else {
            converters.add(DateToLongConverter.INSTANCE);
        }

        converters.add(SerializedObjectToDateConverter.INSTANCE);

        converters.add(CalendarToLongConverter.INSTANCE);
        converters.add(NumberToCalendarConverter.INSTANCE);

        return converters;
    }

    @ReadingConverter
    public enum SerializedObjectToDateConverter implements Converter<Object, Date> {
        INSTANCE;

        @Override
        public Date convert(Object source) {
            if (source == null) {
                return null;
            }
            if (source instanceof Number) {
                Date date = new Date();
                date.setTime(((Number) source).longValue());
                return date;
            } else if (source instanceof String) {
                return Date.from(Instant.parse((String) source).atZone(systemDefault()).toInstant());
            } else {
                // Unsupported serialized object
                return null;
            }
        }
    }

    @WritingConverter
    public enum DateToStringConverter implements Converter<Date, String> {
        INSTANCE;

        @Override
        public String convert(Date source) {
            return source == null ? null : source.toInstant().toString();
        }
    }

    @WritingConverter
    public enum DateToLongConverter implements Converter<Date, Long> {
        INSTANCE;

        @Override
        public Long convert(Date source) {
            return source == null ? null : source.getTime();
        }
    }

    @WritingConverter
    public enum CalendarToLongConverter implements Converter<Calendar, Long> {
        INSTANCE;

        @Override
        public Long convert(Calendar source) {
            return source == null ? null : source.getTimeInMillis() / 1000;
        }
    }

    @ReadingConverter
    public enum NumberToCalendarConverter implements Converter<Number, Calendar> {
        INSTANCE;

        @Override
        public Calendar convert(Number source) {
            if (source == null) {
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(source.longValue() * 1000);
            return calendar;
        }
    }
}
