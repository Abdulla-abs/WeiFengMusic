package com.wei.music.database.typeconverter;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchHistoryConverters {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    @TypeConverter
    public String convertDateToString(Date date) {
        return sdf.format(date);
    }

    @TypeConverter
    public Date convertStringToDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            Calendar instance = Calendar.getInstance();
            instance.set(0, 1, 1);
            return instance.getTime();
        }
    }
}
