package com.mcal.moddedpe.ads.ironsource.data.model.mapper

import com.mcal.moddedpe.ads.ironsource.data.model.domain.AdModel
import com.mcal.moddedpe.ads.ironsource.data.model.remote.AdModelNT

fun AdModelNT.toDomain(): AdModel {
    return AdModel(
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