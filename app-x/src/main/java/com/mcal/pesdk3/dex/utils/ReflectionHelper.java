package com.mcal.pesdk3.dex.utils;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelper {
    public static @NotNull String getClassStructureString(@NotNull Class cls, Object obj, String str, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(cls);
        sb.append("\n");
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                sb.append(str);
                sb.append("  ");
                sb.append(field);
                sb.append(" = ");
                sb.append(field.get(obj));
                sb.append("\n");
            } catch (IllegalAccessException e) {
                sb.append(e);
                sb.append("\n");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Constructor<?>[] declaredConstructors = cls.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {
            sb.append(str);
            sb.append("  ");
            sb.append(constructor);
            sb.append("\n");
        }
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            sb.append(str);
            sb.append("  ");
            sb.append(method);
            sb.append("\n");
        }
        if (!(!z || cls.getSuperclass() == Object.class || cls.getSuperclass() == null)) {
            sb.append("\n");
            sb.append(getClassStructureString(cls.getSuperclass(), obj, str, z));
        }
        return sb.toString();
    }

    public static void printClassStructure(Class cls, Object obj, String str, String str2, boolean z) {
        String[] split = getClassStructureString(cls, obj, str2, z).split("\n");
        for (String str3 : split) {
            Log.i(str, str3);
        }
    }

    public static @Nullable Method getDeclaredMethod(@NotNull Class cls, String str, Class... clsArr) {
        try {
            return cls.getDeclaredMethod(str, clsArr);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(Object obj, @NotNull Class cls, String str, Class[] clsArr, Object[] objArr) throws NoSuchMethodException {
        try {
            return cls.getDeclaredMethod(str, clsArr).invoke(obj, objArr);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}