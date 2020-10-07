package com.microsoft.xbox.toolkit;

import android.view.View;

import com.mcal.mcpelauncher.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLERValueHelper {
    public static int getStringRValue(String name) {
        try {
            return getStringRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getDrawableRValue(String name) {
        try {
            return getDrawableRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getIdRValue(String name) {
        try {
            return getIdRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getStyleRValue(String name) {
        try {
            return getStyleRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    @Nullable
    public static int[] getStyleableRValueArray(String name) {
        try {
            return (int[]) getStyleableRClass().getDeclaredField(name).get((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return null;
        }
    }

    public static int getStyleableRValue(String name) {
        try {
            return getStyleableRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getLayoutRValue(String name) {
        try {
            return getLayoutRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getDimenRValue(String name) {
        try {
            return getDimenRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int getColorRValue(String name) {
        try {
            return getColorRClass().getDeclaredField(name).getInt((Object) null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public static int findDimensionIdByName(String name) {
        Field field = null;
        try {
            field = R.dimen.class.getField(name);
        } catch (NoSuchFieldException e) {
        }
        if (field == null) {
            return -1;
        }
        try {
            return field.getInt(null);
        } catch (IllegalAccessException e2) {
            return -1;
        }
    }

    public static View findViewByString(String viewName) {
        Field field = null;
        try {
            field = R.id.class.getField(viewName);
        } catch (NoSuchFieldException e) {
        }
        int id = -1;
        if (field != null) {
            try {
                id = field.getInt(null);
            } catch (IllegalAccessException e2) {
            }
        }
        return XboxTcuiSdk.getActivity().findViewById(id);
    }

    protected static Class getStringRClass() {
        return R.string.class;
    }

    protected static Class getDrawableRClass() {
        return R.drawable.class;
    }

    protected static Class getIdRClass() {
        return R.id.class;
    }

    protected static Class getStyleRClass() {
        return R.style.class;
    }

    protected static Class getStyleableRClass() {
        return R.styleable.class;
    }

    protected static Class getLayoutRClass() {
        return R.layout.class;
    }

    protected static Class getDimenRClass() {
        return R.dimen.class;
    }

    protected static Class getColorRClass() {
        return R.color.class;
    }
}
