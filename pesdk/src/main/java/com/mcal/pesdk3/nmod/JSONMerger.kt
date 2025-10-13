/*
 * Copyright (C) 2018-2025 Тимашков Иван
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
package com.mcal.pesdk3.nmod

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class JSONMerger(
    private val src: String,
    private val dist: String
) {
    fun merge(): String {
        val srcJson = parseJsonElement(src)
        val distJson = parseJsonElement(dist)

        return when (srcJson) {
            is JsonObject if distJson is JsonObject ->
                mergeObject(srcJson, distJson).toString()

            is JsonArray if distJson is JsonArray ->
                mergeArray(srcJson, distJson).toString()

            else -> throw IllegalArgumentException("Merging FAILED: Incompatible JSON types")
        }
    }

    private fun parseJsonElement(json: String): JsonElement {
        return try {
            Json.parseToJsonElement(json)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON string", e)
        }
    }

    private fun mergeObject(object1: JsonObject, object2: JsonObject): JsonObject {
        val result = object1.toMutableMap()
        object2.forEach { (key, value) ->
            when {
                result.containsKey(key) -> {
                    val existingValue = result[key]!!
                    result[key] = when (existingValue) {
                        is JsonObject if value is JsonObject ->
                            mergeObject(existingValue, value)

                        is JsonArray if value is JsonArray ->
                            mergeArray(existingValue, value)

                        else -> value
                    }
                }

                else -> result[key] = value
            }
        }

        return JsonObject(result)
    }

    private fun mergeArray(array1: JsonArray, array2: JsonArray): JsonArray {
        val result = array1.toMutableList()
        result.addAll(array2)
        return JsonArray(result)
    }
}
