package com.mcal.moddedpe.ads.ironsource.data.model.domain

import kotlinx.serialization.SerialName

data class AdModel(
    val interIsId: String,
    val openAdsAdmobId: String,
    val interAdmobId: String,
    val interAdmobDelay: Int,
    val nativeAdmobId: String,
    val nativeAdmobWidth: Int,
    val nativeAdmobHeight: Int,
    val nativeAdmobClickWidth: Int,
    val nativeAdmobClickHeight: Int,
    val nativeAdmobReloadTime: Int,
    val mrecAdmobId: String,
    val randomTimeFrom: Int,
    val randomTimeTo: Int,
    val status: Int,
)
