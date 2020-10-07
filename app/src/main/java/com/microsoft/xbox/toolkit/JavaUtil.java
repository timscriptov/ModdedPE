package com.microsoft.xbox.toolkit;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.text.format.DateUtils;
import android.view.View;

import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.apache.http.protocol.HTTP;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class JavaUtil {
    private static final String HEX_PREFIX = "0x";
    private static final NumberFormat INTEGER_FORMATTER = NumberFormat.getIntegerInstance(Locale.getDefault());
    private static final Date MIN_DATE = new Date(100, 1, 1);
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance(Locale.getDefault());

    public static String getShortClassName(@NotNull Class cls) {
        String[] tokens = cls.getName().split("\\.");
        return tokens[tokens.length - 1];
    }

    public static boolean stringsEqual(String lhs, String rhs) {
        if ((lhs == null && rhs == null) || lhs == rhs) {
            return true;
        }
        return stringsEqualNonNull(lhs, rhs);
    }

    public static boolean stringsEqualNonNull(String lhs, String rhs) {
        boolean z = false;
        if (lhs == null || rhs == null) {
            return false;
        }
        if (!(lhs == null || rhs == null)) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        return lhs.equals(rhs);
    }

    public static boolean stringsEqualCaseInsensitive(String lhs, String rhs) {
        boolean z = true;
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return lhs.equalsIgnoreCase(rhs);
    }

    public static boolean stringsEqualNonNullCaseInsensitive(String lhs, String rhs) {
        boolean z = true;
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return lhs.equalsIgnoreCase(rhs);
    }

    public static boolean tryParseBoolean(String booleanString, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(booleanString);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int tryParseInteger(String integerString, int defaultValue) {
        try {
            return Integer.parseInt(integerString);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long tryParseLong(String longString, long defaultValue) {
        try {
            return Long.parseLong(longString);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double tryParseDouble(String doubleString, double defaultValue) {
        try {
            return Double.parseDouble(doubleString);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Nullable
    public static String getLocalizedDateString(Date date) {
        try {
            return DateUtils.formatDateTime(XboxTcuiSdk.getApplicationContext(), date.getTime(), 131088);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTimeStringMMSS(long timeInSeconds) {
        return DateUtils.formatElapsedTime(timeInSeconds);
    }

    public static int parseInteger(String intString) {
        try {
            return Integer.parseInt(intString, 10);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean parseBoolean(String boolString) {
        try {
            return Boolean.parseBoolean(boolString);
        } catch (Exception e) {
            return false;
        }
    }

    public static long parseHexLong(String hexLong) {
        if (hexLong == null) {
            return 0;
        }
        if (hexLong.startsWith(HEX_PREFIX)) {
            return parseHexLongExpectHex(hexLong);
        }
        try {
            return Long.parseLong(hexLong, 16);
        } catch (Exception e) {
            return 0;
        }
    }

    private static long parseHexLongExpectHex(@NotNull String hexLong) {
        XLEAssert.assertTrue(hexLong.startsWith(HEX_PREFIX));
        try {
            return Long.parseLong(hexLong.substring(HEX_PREFIX.length()), 16);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isTouchPointInsideView(float touchRawX, float touchRawY, @NotNull View view) {
        int[] coordinates = new int[2];
        view.getLocationOnScreen(coordinates);
        return new Rect(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight()).contains((int) touchRawX, (int) touchRawY);
    }

    @Nullable
    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Nullable
    public static String urlDecode(String src) {
        try {
            return URLDecoder.decode(src, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String stringToUpper(String value) {
        if (value == null) {
            return null;
        }
        return value.toUpperCase();
    }

    public static String stringToLower(String value) {
        if (value == null) {
            return null;
        }
        return value.toLowerCase();
    }

    public static boolean containsFlag(int value, int flagToCheck) {
        return (value & flagToCheck) == flagToCheck;
    }

    @NotNull
    public static String concatenateStringsWithDelimiter(String str1, String str2, String str3, String delimiter) {
        return concatenateStringsWithDelimiter(str1, str2, str3, delimiter, true);
    }

    @NotNull
    public static String concatenateStringsWithDelimiter(String str1, String str2, String str3, String delimiter, boolean addSpaceBeforeDelimiter) {
        String delimiter2 = (addSpaceBeforeDelimiter ? " " : "") + delimiter + " ";
        StringBuilder sb = new StringBuilder();
        if (!isNullOrEmpty(str1)) {
            sb.append(str1);
        }
        if (!isNullOrEmpty(str2)) {
            if (sb.length() > 0) {
                sb.append(delimiter2);
            }
            sb.append(str2);
        }
        if (!isNullOrEmpty(str3)) {
            if (sb.length() > 0) {
                sb.append(delimiter2);
            }
            sb.append(str3);
        }
        return sb.toString();
    }

    @NotNull
    public static String concatenateStringsWithDelimiter(String delimiter, boolean addSpaceBeforeDelimiter, @NotNull String... strs) {
        String delimiter2 = (addSpaceBeforeDelimiter ? " " : "") + delimiter + " ";
        StringBuilder sb = new StringBuilder();
        if (strs.length == 0) {
            return "";
        }
        for (int idx = 0; idx < strs.length; idx++) {
            if (!isNullOrEmpty(strs[idx])) {
                if (sb.length() > 0) {
                    sb.append(delimiter2);
                }
                sb.append(strs[idx]);
            }
        }
        return sb.toString();
    }

    @NotNull
    public static String concatenateUrlWithLinkAndParam(String link, String param, String tag) {
        StringBuffer buffer = new StringBuffer();
        if (!isNullOrEmpty(link)) {
            buffer.append(link);
        }
        if (!isNullOrEmpty(param)) {
            if (buffer.length() > 0) {
                buffer.append(tag);
            }
            buffer.append(param);
        }
        return buffer.toString();
    }

    @Nullable
    public static Date JSONDateToJavaDate(String input) {
        if (isNullOrEmpty(input)) {
            return null;
        }
        XLEAssert.assertTrue(input.substring(0, 6).equals("/Date("));
        int endidx = input.length();
        if (input.substring(input.length() - 7, input.length()).equals("+0000)/")) {
            endidx = input.length() - 7;
        } else if (input.substring(input.length() - 2, input.length()).equals(")/")) {
            endidx = input.length() - 2;
        } else {
            XLEAssert.assertTrue(false);
        }
        return new Date(Long.parseLong(input.substring(6, endidx)));
    }

    @SuppressLint("DefaultLocale")
    @NotNull
    public static String JavaDateToJSONDate(Date javadate) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTime(javadate);
        return String.format("/Date(%d)/", new Object[]{Long.valueOf(calendar.getTimeInMillis())});
    }

    @NotNull
    public static <T> List<T> listIteratorToList(ListIterator<T> i) {
        ArrayList<T> rv = new ArrayList<>();
        while (i != null && i.hasNext()) {
            rv.add(i.next());
        }
        return rv;
    }

    public static String pluralize(int count, String zeroString, String oneString, String nString) {
        switch (count) {
            case 0:
                return zeroString;
            case 1:
                return oneString;
            default:
                return String.format(nString, new Object[]{Integer.valueOf(count)});
        }
    }

    public static int randInRange(@NotNull Random r, int min, int max) {
        XLEAssert.assertTrue(max >= min);
        return r.nextInt(max - min) + min;
    }

    @NotNull
    public static <T> ArrayList<T> sublistShuffle(ArrayList<T> srcarray, int dstsize) {
        boolean z;
        boolean z2 = true;
        Random r = new Random();
        ArrayList<T> rv = new ArrayList<>(dstsize);
        if (!(srcarray == null || srcarray.size() == 0)) {
            if (srcarray.size() >= dstsize) {
                for (int i = 0; i < dstsize; i++) {
                    int swapi = randInRange(r, i, srcarray.size());
                    T tmp = srcarray.get(i);
                    srcarray.set(i, srcarray.get(swapi));
                    srcarray.set(swapi, tmp);
                    rv.add(srcarray.get(i));
                }
            } else {
                if (srcarray.size() <= 0 || srcarray.size() >= dstsize) {
                    z = false;
                } else {
                    z = true;
                }
                XLEAssert.assertTrue(z);
                for (int i2 = 0; i2 < dstsize; i2++) {
                    rv.add(srcarray.get(r.nextInt(srcarray.size())));
                }
            }
            if (rv.size() != dstsize) {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
        }
        return rv;
    }

    @NotNull
    public static String formatInteger(int i) {
        return INTEGER_FORMATTER.format((long) i);
    }

    @NotNull
    public static String formatPercent(float f) {
        XLEAssert.assertTrue(f + " is not between 0 and 1", f >= 0.0f && f <= 1.0f);
        return PERCENT_FORMATTER.format((double) f);
    }

    public static void sleepDebug(long ms) {
    }

    public static int[] concatIntArrays(int[]... arrays) {
        if (arrays == null) {
            return null;
        }
        int finalSize = 0;
        for (int[] a : arrays) {
            finalSize += a.length;
        }
        int[] destArray = new int[finalSize];
        int destPos = 0;
        for (int[] a2 : arrays) {
            System.arraycopy(a2, 0, destArray, destPos, a2.length);
            destPos += a2.length;
        }
        return destArray;
    }

    public static <T> boolean DeepCompareArrayList(ArrayList<T> one, ArrayList<T> two) {
        if (one == two) {
            return true;
        }
        if (one == null) {
            if (two != null) {
                return false;
            }
            return true;
        } else if (two == null) {
            return false;
        } else {
            if (one.size() != two.size()) {
                return false;
            }
            for (int i = 0; i < one.size(); i++) {
                if (!one.get(i).equals(two.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    @NotNull
    @Contract(pure = true)
    public static String surroundInQuotes(String str) {
        return "\"" + str + "\"";
    }

    public static String EnsureEncode(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String str2 = str;
        try {
            return URLEncoder.encode(URLDecoder.decode(str, HTTP.UTF_8), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return str2;
        }
    }

    public static <T> boolean move(ArrayList<T> data, int fromPosition, int toPosition) {
        if (data == null || !isPositionInRange(data, fromPosition) || !isPositionInRange(data, toPosition)) {
            return false;
        }
        T dataFrom = data.get(fromPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                data.set(i, data.get(i + 1));
            }
        } else {
            for (int i2 = fromPosition; i2 > toPosition; i2--) {
                data.set(i2, data.get(i2 - 1));
            }
        }
        data.set(toPosition, dataFrom);
        return true;
    }

    private static <T> boolean isPositionInRange(ArrayList<T> data, int position) {
        return position >= 0 && position < data.size();
    }

    @NotNull
    public static String getCurrentStackTraceAsString() {
        StringBuilder builder = new StringBuilder();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements != null) {
            int length = elements.length;
            for (int i = 0; i < length; i++) {
                builder.append("\n\n \t " + elements[i].toString());
            }
        }
        return builder.toString();
    }

    public static Date convertToUTC(Date local) {
        if (local == null) {
            return null;
        }
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance();
        cal.setTime(local);
        cal.add(14, -tz.getOffset(local.getTime()));
        return cal.getTime();
    }

    public static boolean setFieldValue(@NotNull Object to, String field, Object value) {
        try {
            Field f = to.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(to, value);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
