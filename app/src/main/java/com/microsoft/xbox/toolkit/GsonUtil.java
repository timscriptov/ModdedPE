package com.microsoft.xbox.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class GsonUtil {

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass) {
        return deserializeJson(createMinimumGsonBuilder().create(), stream, resultClass);
    }

    public static <T> T deserializeJson(String input, Class<T> resultClass) {
        return deserializeJson(createMinimumGsonBuilder().create(), input, resultClass);
    }

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass, Type typeForAdapter, Object typeAdapter) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(typeForAdapter, typeAdapter).create(), stream, resultClass);
    }

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass, @NotNull Map<Type, Object> adapters) {
        GsonBuilder builder = createMinimumGsonBuilder();
        for (Map.Entry<Type, Object> e : adapters.entrySet()) {
            builder.registerTypeAdapter(e.getKey(), e.getValue());
        }
        return deserializeJson(builder.create(), stream, resultClass);
    }

    public static <T> T deserializeJson(String input, Class<T> resultClass, Type typeForAdapter, Object typeAdapter) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(typeForAdapter, typeAdapter).create(), input, resultClass);
    }

    public static <T> T deserializeJson(Gson gson, InputStream stream, Class<T> resultClass) {
        InputStreamReader iReader = null;
        BufferedReader bReader = null;
        T result = null;
        try {
            InputStreamReader iReader2 = new InputStreamReader(stream);
            try {
                BufferedReader bReader2 = new BufferedReader(iReader2);
                try {
                    result = gson.fromJson(bReader2, resultClass);
                    if (bReader2 != null) {
                        try {
                            bReader2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (iReader2 != null) {
                        try {
                            iReader2.close();
                            BufferedReader bufferedReader = bReader2;
                            InputStreamReader inputStreamReader = iReader2;
                        } catch (Exception e2) {
                            BufferedReader bufferedReader2 = bReader2;
                            InputStreamReader inputStreamReader2 = iReader2;
                        }
                    } else {
                        InputStreamReader inputStreamReader3 = iReader2;
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                } catch (Throwable th) {
                    throw th;
                }
            } catch (Exception e4) {
                iReader = iReader2;
                if (bReader != null) {
                    try {
                        bReader.close();
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
                if (iReader != null) {
                    try {
                        iReader.close();
                    } catch (Exception e6) {
                        e6.printStackTrace();
                    }
                }
                return result;
            } catch (Throwable th2) {
                iReader = iReader2;
                if (bReader != null) {
                    try {
                        bReader.close();
                    } catch (Exception e7) {
                        e7.printStackTrace();
                    }
                }
                if (iReader != null) {
                    try {
                        iReader.close();
                    } catch (Exception e8) {
                        e8.printStackTrace();
                    }
                }
            }
        } catch (Exception e9) {
            e9.printStackTrace();
            return result;
        } catch (Throwable th3) {
            th3.printStackTrace();
        }
        return result;
    }

    @Nullable
    public static <T> T deserializeJson(Gson gson, String input, Class<T> resultClass) {
        try {
            return gson.fromJson(input, resultClass);
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    public static GsonBuilder createMinimumGsonBuilder() {
        return new GsonBuilder().excludeFieldsWithModifiers(128);
    }

    public static String toJsonString(Object obj) {
        return new Gson().toJson(obj);
    }

    @NotNull
    public static String buildJsonBody(JsonBodyBuilder builder) throws IOException {
        JsonWriter w;
        StringWriter out = new StringWriter();
        try {
            w = new JsonWriter(out);
            builder.buildBody(w);
            String stringWriter = out.toString();
            w.close();
            out.close();
            return stringWriter;
        } catch (Throwable th) {
            out.close();
            throw th;
        }
    }

    public interface JsonBodyBuilder {
        void buildBody(JsonWriter jsonWriter) throws IOException;
    }
}
