package com.mcal.moddedpe.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdConfigModelNT(
    @SerialName("inter_is_id") val interIsId: String,
    @SerialName("open_ads_admob_id") val openAdsAdmobId: String,
    @SerialName("inter_admob_id") val interAdmobId: String,
    @SerialName("inter_admob_delay") val interAdmobDelay: Int,
    @SerialName("native_admob_id") val nativeAdmobId: String,
    @SerialName("native_admob_width") val nativeAdmobWidth: Int,
    @SerialName("native_admob_height") val nativeAdmobHeight: Int,
    @SerialName("native_admob_click_width") val nativeAdmobClickWidth: Int,
    @SerialName("native_admob_click_height") val nativeAdmobClickHeight: Int,
    @SerialName("native_admob_reload_time") val nativeAdmobReloadTime: Int,
    @SerialName("mrec_admob_id") val mrecAdmobId: String,
    @SerialName("random_time_from") val randomTimeFrom: Int,
    @SerialName("random_time_to") val randomTimeTo: Int,
    @SerialName("status") val status: Int,
)
