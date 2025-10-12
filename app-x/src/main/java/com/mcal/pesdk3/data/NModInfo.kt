package com.mcal.pesdk3.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NModInfo(
    @SerialName("native_libs_info")
    val nativeLibsInfo: Array<NModLibInfo>? = null,
    @SerialName("text_edit")
    val textEdit: Array<NModTextEditBean>? = null,
    @SerialName("json_edit")
    val jsonEdit: Array<NModJsonEditBean>? = null,
    @SerialName("version_code")
    var versionCode: Int = -1,
    @SerialName("name")
    val name: String? = null,
    @SerialName("package_name")
    var packageName: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("author")
    val author: String? = null,
    @SerialName("version_name")
    var versionName: String? = null,
    @SerialName("banner_title")
    val bannerTitle: String? = null,
    @SerialName("banner_image_path")
    val bannerImagePath: String? = null,
    @SerialName("change_log")
    val changeLog: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NModInfo

        if (versionCode != other.versionCode) return false
        if (!nativeLibsInfo.contentEquals(other.nativeLibsInfo)) return false
        if (!textEdit.contentEquals(other.textEdit)) return false
        if (!jsonEdit.contentEquals(other.jsonEdit)) return false
        if (name != other.name) return false
        if (packageName != other.packageName) return false
        if (description != other.description) return false
        if (author != other.author) return false
        if (versionName != other.versionName) return false
        if (bannerTitle != other.bannerTitle) return false
        if (bannerImagePath != other.bannerImagePath) return false
        if (changeLog != other.changeLog) return false

        return true
    }

    override fun hashCode(): Int {
        var result = versionCode
        result = 31 * result + (nativeLibsInfo?.contentHashCode() ?: 0)
        result = 31 * result + (textEdit?.contentHashCode() ?: 0)
        result = 31 * result + (jsonEdit?.contentHashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (packageName?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (versionName?.hashCode() ?: 0)
        result = 31 * result + (bannerTitle?.hashCode() ?: 0)
        result = 31 * result + (bannerImagePath?.hashCode() ?: 0)
        result = 31 * result + (changeLog?.hashCode() ?: 0)
        return result
    }
}