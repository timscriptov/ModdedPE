package com.mcal.moddedpe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.core.data.StorageHelper.behaviorPackFile
import com.mcal.core.data.StorageHelper.mainPackFile
import com.mcal.core.data.StorageHelper.resourcePackFile
import com.mcal.core.data.StorageHelper.version
import com.mcal.moddedpe.base.BaseActivity
import com.mcal.moddedpe.data.DownloadItem
import com.mcal.moddedpe.data.ResourceType
import com.mcal.moddedpe.databinding.ActivityLoadingPackBinding
import kotlinx.coroutines.*
import okhttp3.*
import okio.buffer
import okio.sink
import java.io.IOException

class LoadingActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoadingPackBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (installedBehaviorPack && installedMainPack && installedResourcePack) {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        } else {
            if (!installedResourcePack) {
                val resourcePackFile = resourcePackFile(this)
                if (resourcePackFile.exists()) {
                    resourcePackFile.delete()
                }
                download(
                    DownloadItem(
                        ResourceType.RESOURCE,
                        "https://github.com/TimScriptov/lokicraft/raw/main/$version/resource_pack_$version.zip",
                        resourcePackFile,
                        binding.progressBarResourcePack,
                        binding.progressTextLengthResourcePack
                    )
                )
            } else {
                (binding.progressBarResourcePack.parent as? ViewGroup)?.visibility = View.GONE
            }
            if (!installedMainPack) {
                val mainPackFile = mainPackFile(this)
                if (mainPackFile.exists()) {
                    mainPackFile.delete()
                }
                download(
                    DownloadItem(
                        ResourceType.MAIN,
                        "https://github.com/TimScriptov/lokicraft/raw/main/$version/main_pack_$version.zip",
                        mainPackFile,
                        binding.progressBarMainPack,
                        binding.progressTextLengthMainPack
                    )
                )
            } else {
                (binding.progressBarMainPack.parent as? ViewGroup)?.visibility = View.GONE
            }
            if (!installedBehaviorPack) {
                val behaviorPackFile = behaviorPackFile(this)
                if (behaviorPackFile.exists()) {
                    behaviorPackFile.delete()
                }
                download(
                    DownloadItem(
                        ResourceType.BEHAVIOR,
                        "https://github.com/TimScriptov/lokicraft/raw/main/$version/behavior_pack_$version.zip",
                        behaviorPackFile,
                        binding.progressBarBehaviorPack,
                        binding.progressTextLengthBehaviorPack
                    )
                )
            } else {
                (binding.progressBarBehaviorPack.parent as? ViewGroup)?.visibility = View.GONE
            }
            CoroutineScope(Dispatchers.IO).launch {
                repeat(Int.MAX_VALUE) {
                    withContext(Dispatchers.Main) {
                        if (installedResourcePack && installedBehaviorPack && installedMainPack) {
                            startActivity(
                                Intent(
                                    this@LoadingActivity,
                                    GameActivity::class.java
                                )
                            )
                            finish()
                        }
                    }
                    delay(5000L)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!installedResourcePack) {
            val resourcePackFile = resourcePackFile(this)
            if (resourcePackFile.exists()) {
                resourcePackFile.delete()
            }
        }
        if (!installedMainPack) {
            val mainPackFile = mainPackFile(this)
            if (mainPackFile.exists()) {
                mainPackFile.delete()
            }
        }
        if (!installedBehaviorPack) {
            val behaviorPackFile = behaviorPackFile(this)
            if (behaviorPackFile.exists()) {
                behaviorPackFile.delete()
            }
        }
    }

    private fun download(item: DownloadItem) {
        val request = Request.Builder()
            .url(item.url)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val resourcePackFile = item.file
                    if (resourcePackFile.exists()) {
                        resourcePackFile.delete()
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        showDialog()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    response.body?.let { body ->
                        val contentLength = body.contentLength().toInt()
                        body.source().use { sourceBytes ->
                            val sink = item.file.sink().buffer()
                            var totalRead = 0L
                            var lastRead: Long
                            while (sourceBytes
                                    .read(sink.buffer, 8L * 1024)
                                    .also { lastRead = it } != -1L
                            ) {
                                totalRead += lastRead
                                sink.emitCompleteSegments()
                                updateProgress(item, contentLength, totalRead)
                            }
                            sink.writeAll(sourceBytes)
                            sink.close()
                            finishDownloading(item)
                        }
                    }
                }
            })
        }
    }

    private fun finishDownloading(item: DownloadItem) {
        when (item.type) {
            ResourceType.RESOURCE -> {
                installedResourcePack = true
            }
            ResourceType.BEHAVIOR -> {
                installedBehaviorPack = true
            }
            ResourceType.MAIN -> {
                installedMainPack = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(item: DownloadItem, contentLength: Int, totalRead: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            item.progressBarView.visibility = View.VISIBLE
            if (contentLength != -1) {
                item.progressBarView.progress = (totalRead * 100 / contentLength).toInt()
            } else {
                item.progressBarView.isIndeterminate = true
            }
            val total = totalRead / (1024 * 1024)
            val length = contentLength / (1024 * 1024)
            item.textLengthView.text = "$total / $length MB"
        }
    }

    private fun showDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            MaterialAlertDialogBuilder(this@LoadingActivity).apply {
                setMessage(R.string.download_resource_pack_failed)
                setPositiveButton(R.string.download_repeat) { dialog, _ ->
                    dialog.dismiss()
                    recreate()
                }
                setNegativeButton(R.string.exit) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
            }.show()
        }
    }
}
