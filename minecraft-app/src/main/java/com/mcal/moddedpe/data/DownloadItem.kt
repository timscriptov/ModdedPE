package com.mcal.moddedpe.data

import android.widget.ProgressBar
import android.widget.TextView
import java.io.File

class DownloadItem(
    @JvmField
    var type: ResourceType,
    @JvmField
    val url: String,
    @JvmField
    val file: File,
    @JvmField
    val progressBarView: ProgressBar,
    @JvmField
    val textLengthView: TextView
)
