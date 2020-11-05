package com.example.weightchangetracker.util;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateConverters {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd-MM");

    @TypeConverter
    public static OffsetDateTime fromTimestamp(String value) {
        return value == null ? null : formatter.parse(value, OffsetDateTime::from);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static String dateToTimestamp(OffsetDateTime date) {
        return date == null ? null : date.format(formatter);
    }

    public static OffsetDateTime fromDayMonthYear(int year, int month, int dayOfMonth) {
        return OffsetDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0,
                OffsetDateTime.now().getOffset());
    }

    public static float dateToFloat(OffsetDateTime date) {
        return date == null ? 0 : date.toEpochSecond();
    }

    public static String dateToStringDayMonthYear(OffsetDateTime date) {
        return date == null ? "" : date.format(localDateFormatter);
    }
    public static String floatToStringShort(float date) {
        OffsetDateTime offsetDate =
                OffsetDateTime.of(LocalDateTime.ofEpochSecond((long)date, 0, OffsetDateTime.now().getOffset()),
                        OffsetDateTime.now().getOffset());

        return offsetDate.format(shortDateFormatter);
    }

}
