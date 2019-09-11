/*
 * Copyright (C) 2018-2019 Тимашков Иван
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
package com.mcal.pesdk.nmod

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class JSONMerger(private val mSrc: String, private val mDist: String) {

    private fun isJSONArray(json: String): Boolean {
        try {
            JSONArray(json)
            return true
        } catch (e: JSONException) {
            return false
        }

    }

    private fun isJSONObject(json: String): Boolean {
        try {
            JSONObject(json)
            return true
        } catch (e: JSONException) {
            return false
        }

    }

    @Throws(JSONException::class)
    private fun mergeObject(object1: JSONObject, object2: JSONObject): JSONObject {
        val iterator = object2.keys()
        while (iterator.hasNext()) {
            val name = iterator.next() as String
            judgeTypeAndPut(object1, object2, name)
        }
        return object1
    }

    @Throws(JSONException::class)
    private fun mergeArray(array1: JSONArray, array2: JSONArray): JSONArray {
        for (index in 0 until array2.length()) {
            judgeTypeAndPut(array1, array2, index)
        }
        return array1
    }

    @Throws(JSONException::class)
    private fun judgeTypeAndPut(array1: JSONArray, array2: JSONArray, index: Int) {
        if (isTypeJSONArray(array2, index)) {
            array1.put(array2.getJSONArray(index))
        } else if (isTypeJSONObject(array2, index)) {
            array1.put(array2.getJSONObject(index))
        } else if (isTypeJSONString(array2, index)) {
            array1.put(array2.getString(index))
        } else if (isTypeJSONInteger(array2, index)) {
            array1.put(array2.getInt(index))
        } else
            throw JSONException("ERROR: CANNOT JUDGE ITEM TYPE.")
    }

    @Throws(JSONException::class)
    private fun judgeTypeAndPut(object1: JSONObject, object2: JSONObject, key: String) {
        if (isTypeJSONArray(object2, key)) {
            if (object1.has(key)) {
                object1.put(key, mergeArray(object1.getJSONArray(key), object2.getJSONArray(key)))
            } else {
                object1.put(key, object2.getJSONArray(key))
            }
        } else if (isTypeJSONObject(object2, key)) {
            if (object1.has(key)) {
                object1.put(key, mergeObject(object1.getJSONObject(key), object2.getJSONObject(key)))
            } else {
                object1.put(key, object2.getJSONObject(key))
            }
        } else if (isTypeJSONString(object2, key)) {
            object1.put(key, object2.getString(key))
        } else if (isTypeJSONInteger(object2, key)) {
            object1.put(key, object2.getInt(key))
        } else
            throw JSONException("ERROR:CANNOT JUDGE ITEM TYPE.")
    }

    private fun isTypeJSONArray(src: JSONObject, key: String): Boolean {
        return try {
            src.getJSONArray(key)
            true
        } catch (jsonE: JSONException) {
            false
        }

    }

    private fun isTypeJSONObject(src: JSONObject, key: String): Boolean {
        try {
            src.getJSONObject(key)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONString(src: JSONObject, key: String): Boolean {
        try {
            src.getString(key)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONInteger(src: JSONObject, key: String): Boolean {
        try {
            src.getInt(key)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONArray(src: JSONArray, index: Int): Boolean {
        try {
            src.getJSONArray(index)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONObject(src: JSONArray, index: Int): Boolean {
        try {
            src.getJSONObject(index)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONString(src: JSONArray, index: Int): Boolean {
        try {
            src.getString(index)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    private fun isTypeJSONInteger(src: JSONArray, index: Int): Boolean {
        try {
            src.getInt(index)
            return true
        } catch (jsonE: JSONException) {
            return false
        }

    }

    @Throws(JSONException::class)
    fun merge(): String {
        return if (isJSONObject(mSrc) && isJSONObject(mDist)) {
            mergeObject(JSONObject(mSrc), JSONObject(mDist)).toString()
        } else if (isJSONArray(mSrc) && isJSONArray(mDist)) {
            mergeArray(JSONArray(mSrc), JSONArray(mDist)).toString()
        } else
            throw JSONException("Merging FAILED: CANNOT JUDGE STRING TYPE")
    }
}
