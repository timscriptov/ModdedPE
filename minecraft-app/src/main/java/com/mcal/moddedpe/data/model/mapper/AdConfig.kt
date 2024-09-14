package com.mcal.moddedpe.data.model.mapper

import com.mcal.moddedpe.data.model.domain.AdConfigModel
import com.mcal.moddedpe.data.model.remote.AdConfigModelNT

fun AdConfigModelNT.toDomain(): AdConfigModel {
    return AdConfigModel(
        interIsId = interIsId,
        openAdsAdmobId = openAdsAdmobId,
        interAdmobId = interAdmobId,
        interAdmobDelay = interAdmobDelay,
        nativeAdmobId = nativeAdmobId,
        nativeAdmobWidth = nativeAdmobWidth,
        nativeAdmobHeight = nativeAdmobHeight,
        nativeAdmobClickWidth = nativeAdmobClickWidth,
        nativeAdmobClickHeight = nativeAdmobClickHeight,
        nativeAdmobReloadTime = nativeAdmobReloadTime,
        mrecAdmobId = mrecAdmobId,
        randomTimeFrom = randomTimeFrom,
        randomTimeTo = randomTimeTo,
        status = status,
    )
}