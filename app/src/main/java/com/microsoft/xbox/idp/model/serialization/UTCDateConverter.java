package com.microsoft.xbox.idp.model.serialization;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UTCDateConverter {
    public static final String TAG = UTCDateConverter.class.getSimpleName();
    private static final int NO_MS_STRING_LENGTH = 19;
    public static SimpleDateFormat defaultFormatNoMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateAlternateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
    private static SimpleDateFormat defaultFormatMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    public static synchronized Date convert(String value) {
        Date date = null;
        synchronized (UTCDateConverter.class) {
            if (!TextUtils.isEmpty(value)) {
                if (value.endsWith("Z")) {
                    value = value.replace("Z", "");
                }
                TimeZone timeZone = null;
                if (value.endsWith("+00:00")) {
                    value = value.replace("+00:00", "");
                } else if (value.endsWith("+01:00")) {
                    value = value.replace("+01:00", "");
                    timeZone = TimeZone.getTimeZone("GMT+01:00");
                } else if (value.contains(".")) {
                    value = value.replaceAll("([.][0-9]{3})[0-9]*$", "$1");
                }
                boolean noMsDate = value.length() == NO_MS_STRING_LENGTH;
                if (timeZone == null) {
                    timeZone = TimeZone.getTimeZone("GMT");
                }
                if (noMsDate) {
                    try {
                        defaultFormatNoMs.setTimeZone(timeZone);
                        date = defaultFormatNoMs.parse(value);
                    } catch (ParseException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    defaultFormatMs.setTimeZone(timeZone);
                    try {
                        date = defaultFormatMs.parse(value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return date;
    }

    public static class UTCDateConverterJSONDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
        public Date deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return UTCDateConverter.convert(json.getAsJsonPrimitive().getAsString());
        }

        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(UTCDateConverter.defaultFormatNoMs.format(src));
        }
    }

    public static class UTCDateConverterShortDateFormatJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String raw = json.getAsJsonPrimitive().getAsString();
            UTCDateConverter.shortDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return UTCDateConverter.shortDateFormat.parse(raw);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + raw);
                return null;
            }
        }
    }

    public static class UTCDateConverterShortDateAlternateFormatJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String raw = json.getAsJsonPrimitive().getAsString();
            Date result = null;
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            UTCDateConverter.shortDateFormat.setTimeZone(timeZone);
            try {
                result = UTCDateConverter.shortDateFormat.parse(raw);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse short date " + raw);
            }
            if (result == null || result.getYear() + 1900 >= 2000) {
                return result;
            }
            UTCDateConverter.shortDateAlternateFormat.setTimeZone(timeZone);
            try {
                return UTCDateConverter.shortDateAlternateFormat.parse(raw);
            } catch (ParseException e2) {
                Log.d(UTCDateConverter.TAG, "failed to parse alternate short date " + raw);
                return result;
            }
        }
    }

    public static class UTCRoundtripDateConverterJSONDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String raw = json.getAsJsonPrimitive().getAsString();
            if (raw.endsWith("Z")) {
                raw = raw.replace("Z", "");
            }
            TimeZone timeZone = null;
            if (0 == 0) {
                timeZone = TimeZone.getTimeZone("GMT");
            }
            UTCDateConverter.defaultFormatNoMs.setTimeZone(timeZone);
            try {
                return UTCDateConverter.defaultFormatNoMs.parse(raw);
            } catch (ParseException e) {
                Log.d(UTCDateConverter.TAG, "failed to parse date " + raw);
                return null;
            }
        }
    }
}