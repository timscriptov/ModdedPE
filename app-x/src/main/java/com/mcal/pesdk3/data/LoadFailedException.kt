package com.mcal.pesdk3.data

class LoadFailedException(val type: Int, cause: Throwable?) : Exception(cause) {
    fun toTypeString(): String {
        return when (this.type) {
            TYPE_DECODE_FAILED -> "DECODE_FAILED"
            TYPE_LOAD_LIB_FAILED -> "LOAD_LIB_FAILED"
            TYPE_FILE_NOT_FOUND -> "FILE_NOT_FOUND"
            TYPE_INVALID_SIZE -> "INVALID_SIZE"
            TYPE_IO_FAILED -> "IO_FAILED"
            TYPE_JSON_SYNTAX -> "JSON_SYNTAX"
            else -> "TYPE"
        }
    }

    companion object {
        const val TYPE_LOAD_LIB_FAILED: Int = 1
        const val TYPE_IO_FAILED: Int = 2
        const val TYPE_JSON_SYNTAX: Int = 3
        const val TYPE_FILE_NOT_FOUND: Int = 4
        const val TYPE_DECODE_FAILED: Int = 5
        const val TYPE_INVALID_SIZE: Int = 6
    }
}