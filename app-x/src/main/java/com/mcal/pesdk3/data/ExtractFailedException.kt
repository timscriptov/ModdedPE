package com.mcal.pesdk3.data

class ExtractFailedException(val type: Int, cause: Throwable?) : Exception(cause) {
    fun toTypeString(): String {
        return when (this.type) {
            TYPE_JSON_SYNTAX_EXCEPTION -> "JSON_SYNTAX_EXCEPTION"
            TYPE_INEQUAL_PACKAGE_NAME -> "INEQUAL_PACKAGE_NAME"
            TYPE_DECODE_FAILED -> "DECODE_FAILED"
            TYPE_IO_EXCEPTION -> "IO_EXCEPTION"
            TYPE_INVAILD_PACKAGE_NAME -> "INVAILD_PACKAGE_NAME"
            TYPE_PACKAGE_NOT_FOUND -> "PACKAGE_NOT_FOUND"
            TYPE_NO_MANIFEST -> "NO_MANIFEST"
            TYPE_UNDEFINED_PACKAGE_NAME -> "UNDEFINED_PACKAGE_NAME"
            TYPE_UNEXPECTED -> "UNEXPECTED"
            TYPE_REDUNDANT_MANIFEST -> "REDUNDANT_MANIFEST"
            else -> "null"
        }
    }

    companion object {
        const val TYPE_JSON_SYNTAX_EXCEPTION: Int = 1
        const val TYPE_IO_EXCEPTION: Int = 2
        const val TYPE_NO_MANIFEST: Int = 3
        const val TYPE_PACKAGE_NOT_FOUND: Int = 4
        const val TYPE_UNDEFINED_PACKAGE_NAME: Int = 5
        const val TYPE_INVAILD_PACKAGE_NAME: Int = 6
        const val TYPE_INEQUAL_PACKAGE_NAME: Int = 7
        const val TYPE_DECODE_FAILED: Int = 8
        const val TYPE_UNEXPECTED: Int = 9
        const val TYPE_REDUNDANT_MANIFEST: Int = 10
    }
}