package com.mcal.pesdk3.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NModPreloadBean(
    @SerialName("native_libs")
    var nativeLibs: Array<NModLibInfo>? = null,
    @SerialName("assets_path")
    var assetsPath: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NModPreloadBean

        if (!nativeLibs.contentEquals(other.nativeLibs)) return false
        if (assetsPath != other.assetsPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nativeLibs?.contentHashCode() ?: 0
        result = 31 * result + (assetsPath?.hashCode() ?: 0)
        return result
    }
}