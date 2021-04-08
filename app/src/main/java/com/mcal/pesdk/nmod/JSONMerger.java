/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk.nmod;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class JSONMerger {
    private final String mSrc;
    private final String mDist;

    JSONMerger(String src, String src2) {
        this.mSrc = src;
        this.mDist = src2;
    }

    private static boolean isJSONArray(String json) {
        try {
            new JSONArray(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private static boolean isJSONObject(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    @Contract("_, _ -> param1")
    private static JSONObject mergeObject(JSONObject object1, @NotNull JSONObject object2) throws JSONException {
        Iterator iterator = object2.keys();
        for (; iterator.hasNext(); ) {
            String name = (String) iterator.next();
            judgeTypeAndPut(object1, object2, name);
        }
        return object1;
    }

    @Contract("_, _ -> param1")
    private static JSONArray mergeArray(JSONArray array1, @NotNull JSONArray array2) throws JSONException {
        for (int index = 0; index < array2.length(); ++index) {
            judgeTypeAndPut(array1, array2, index);
        }
        return array1;
    }

    private static void judgeTypeAndPut(JSONArray array1, JSONArray array2, int index) throws JSONException {
        if (isTypeJSONArray(array2, index)) {
            array1.put(array2.getJSONArray(index));
        } else if (isTypeJSONObject(array2, index)) {
            array1.put(array2.getJSONObject(index));
        } else if (isTypeJSONString(array2, index)) {
            array1.put(array2.getString(index));
        } else if (isTypeJSONInteger(array2, index)) {
            array1.put(array2.getInt(index));
        } else
            throw new JSONException("ERROR: CANNOT JUDGE ITEM TYPE.");
    }

    private static void judgeTypeAndPut(JSONObject object1, JSONObject object2, String key) throws JSONException {
        if (isTypeJSONArray(object2, key)) {
            if (object1.has(key)) {
                object1.put(key, mergeArray(object1.getJSONArray(key), object2.getJSONArray(key)));
            } else {
                object1.put(key, object2.getJSONArray(key));
            }
        } else if (isTypeJSONObject(object2, key)) {
            if (object1.has(key)) {
                object1.put(key, mergeObject(object1.getJSONObject(key), object2.getJSONObject(key)));
            } else {
                object1.put(key, object2.getJSONObject(key));
            }
        } else if (isTypeJSONString(object2, key)) {
            object1.put(key, object2.getString(key));
        } else if (isTypeJSONInteger(object2, key)) {
            object1.put(key, object2.getInt(key));
        } else
            throw new JSONException("ERROR:CANNOT JUDGE ITEM TYPE.");
    }

    private static boolean isTypeJSONArray(@NotNull JSONObject src, String key) {
        try {
            src.getJSONArray(key);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONObject(@NotNull JSONObject src, String key) {
        try {
            src.getJSONObject(key);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONString(@NotNull JSONObject src, String key) {
        try {
            src.getString(key);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONInteger(@NotNull JSONObject src, String key) {
        try {
            src.getInt(key);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONArray(@NotNull JSONArray src, int index) {
        try {
            src.getJSONArray(index);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONObject(@NotNull JSONArray src, int index) {
        try {
            src.getJSONObject(index);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONString(@NotNull JSONArray src, int index) {
        try {
            src.getString(index);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    private static boolean isTypeJSONInteger(@NotNull JSONArray src, int index) {
        try {
            src.getInt(index);
            return true;
        } catch (JSONException jsonE) {
            return false;
        }
    }

    String merge() throws JSONException {
        if (isJSONObject(mSrc) && isJSONObject(mDist)) {
            return mergeObject(new JSONObject(mSrc), new JSONObject(mDist)).toString();
        } else if (isJSONArray(mSrc) && isJSONArray(mDist)) {
            return mergeArray(new JSONArray(mSrc), new JSONArray(mDist)).toString();
        } else
            throw new JSONException("Merging FAILED: CANNOT JUDGE STRING TYPE");
    }
}
