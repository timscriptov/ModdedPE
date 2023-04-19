package com.mcal.core.utils

object StringHelper {
    fun xor(str: String, i: Int): String {
        return buildString {
            for (j in str.indices) {
                append((str[j].code xor a(i)[j % a(i).size].code).toChar())
            }
        }
    }

    private fun a(i: Int): CharArray {
        return when (i) {
            0 -> charArrayOf('a', 'z')
            1 -> charArrayOf('\u3005', '\u3006')
            2 -> charArrayOf('\u6033')
            3 -> charArrayOf('a', 'z')
            else -> charArrayOf()
        }
    }
}