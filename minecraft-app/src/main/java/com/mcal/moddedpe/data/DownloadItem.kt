package com.mcal.moddedpe.data

import java.io.File

class DownloadItem(
    @JvmField
    var type: ResourceType,
    @JvmField
    var title: String,
    @JvmField
    val url: String,
    @JvmField
    val file: File,
)
