package com.mcal.moddedpe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.core.data.StorageHelper.VERSION
import com.mcal.core.data.StorageHelper.behaviorPackFile
import com.mcal.core.data.StorageHelper.nativeLibrariesFile
import com.mcal.core.data.StorageHelper.mainPackFile
import com.mcal.core.data.StorageHelper.resourcePackFile
import com.mcal.core.data.StorageHelper.vanillaResourcePackFile
import com.mcal.core.utils.ABIHelper.getABI
import com.mcal.core.utils.NetHelper
import com.mcal.moddedpe.base.BaseActivity
import com.mcal.moddedpe.data.DownloadItem
import com.mcal.moddedpe.data.ResourceType
import com.mcal.moddedpe.databinding.ActivityLoadingPackBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.util.concurrent.TimeUnit

class LoadingActivity : BaseActivity() {
    private val LINK_RESOURCE_PACK =
        "https://github.com/TimScriptov/resource_pack/raw/main/$VERSION/resource_pack.zip"
    private val LINK_VANILLA_RESOURCE_PACK =
        "https://github.com/TimScriptov/resource_pack/raw/main/$VERSION/resource_pack_vanilla.zip"
    private val LINK_BEHAVIOR_PACK =
        "https://github.com/TimScriptov/resource_pack/raw/main/$VERSION/behavior_pack.zip"
    private val LINK_MAIN_PACK =
        "https://github.com/TimScriptov/resource_pack/raw/main/$VERSION/main_pack.zip"
    private val LINK_LIBRARIES =
        "https://github.com/TimScriptov/resource_pack/raw/main/$VERSION/${getABI()}.zip"

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoadingPackBinding.inflate(
            layoutInflater
        )
    }

    override fun onResume() {
        super.onResume()
        notAvailableDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (installedBehaviorPack && installedMainPack && installedResourcePack && installedVanillaResourcePack && installedNative) {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                if (!installedResourcePack) {
                    runOnUiThread {
                        binding.include.progressCount.text = "1 / 5"
                    }
                    val resourcePackFile = resourcePackFile(this@LoadingActivity)
                    if (resourcePackFile.exists()) {
                        resourcePackFile.delete()
                    }
                    download(
                        DownloadItem(
                            ResourceType.RESOURCE,
                            "Downloading resource pack",
                            LINK_RESOURCE_PACK,
                            resourcePackFile,
                        )
                    )
                }
                if (!installedBehaviorPack) {
                    runOnUiThread {
                        binding.include.progressCount.text = "2 / 5"
                    }
                    val behaviorPackFile = behaviorPackFile(this@LoadingActivity)
                    if (behaviorPackFile.exists()) {
                        behaviorPackFile.delete()
                    }
                    download(
                        DownloadItem(
                            ResourceType.BEHAVIOR,
                            "Downloading behavior pack",
                            LINK_BEHAVIOR_PACK,
                            behaviorPackFile,
                        )
                    )
                }
                if (!installedVanillaResourcePack) {
                    runOnUiThread {
                        binding.include.progressCount.text = "3 / 5"
                    }
                    val vanillaResourcePackFile = vanillaResourcePackFile(this@LoadingActivity)
                    if (vanillaResourcePackFile.exists()) {
                        vanillaResourcePackFile.delete()
                    }
                    download(
                        DownloadItem(
                            ResourceType.VANILLA,
                            "Downloading textures pack",
                            LINK_VANILLA_RESOURCE_PACK,
                            vanillaResourcePackFile,
                        )
                    )
                }
                if (!installedMainPack) {
                    runOnUiThread {
                        binding.include.progressCount.text = "4 / 5"
                    }
                    val mainPackFile = mainPackFile(this@LoadingActivity)
                    if (mainPackFile.exists()) {
                        mainPackFile.delete()
                    }
                    download(
                        DownloadItem(
                            ResourceType.MAIN,
                            "Downloading main pack",
                            LINK_MAIN_PACK,
                            mainPackFile,
                        )
                    )
                }
                if (!installedNative) {
                    runOnUiThread {
                        binding.include.progressCount.text = "5 / 5"
                    }
                    val nativeFile = nativeLibrariesFile(this@LoadingActivity)
                    if (nativeFile.exists()) {
                        nativeFile.delete()
                    }
                    download(
                        DownloadItem(
                            ResourceType.LIBS,
                            "Downloading libraries",
                            LINK_LIBRARIES,
                            nativeFile,
                        )
                    )
                }
                runOnUiThread {
                    if (installedResourcePack && installedBehaviorPack && installedMainPack && installedVanillaResourcePack && installedNative) {
                        startActivity(Intent(this@LoadingActivity, GameActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun download(item: DownloadItem) {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(item.url)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.let { body ->
                    val contentLength = body.contentLength().toInt()
                    body.source().use { sourceBytes ->
                        val sink = item.file.sink().buffer()
                        var totalRead = 0L
                        var lastRead: Long
                        while (sourceBytes.read(sink.buffer, 8L * 1024)
                                .also { lastRead = it } != -1L
                        ) {
                            totalRead += lastRead
                            sink.emitCompleteSegments()
                            updateProgress(contentLength, totalRead)
                        }
                        sink.writeAll(sourceBytes)
                        sink.close()
                        finishDownloading(item)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(
        contentLength: Int,
        totalRead: Long,
    ) {
        runOnUiThread {
            binding.include.progressBar.visibility = View.VISIBLE
            val total = totalRead / (1024 * 1024)
            val length = contentLength / (1024 * 1024)
            binding.include.progressLength.text = "$total / $length MB"
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

            ResourceType.VANILLA -> {
                installedVanillaResourcePack = true
            }

            ResourceType.LIBS -> {
                installedNative = true
            }
        }
    }

    private fun notAvailableDialog() {
        var isAvailable: Boolean
        runBlocking {
            isAvailable = NetHelper.isNetworkAvailable(this@LoadingActivity)
        }
        val context = this@LoadingActivity
        if (!isAvailable) {
            MaterialAlertDialogBuilder(context).apply {
                setMessage(getString(R.string.dialog_message_not_available_internet))
                setCancelable(false)
                setPositiveButton(getString(R.string.btn_check_internet)) { d, _ ->
                    runBlocking {
                        isAvailable = NetHelper.isNetworkAvailable(this@LoadingActivity)
                    }
                    if (isAvailable) {
                        d.dismiss()
                    } else {
                        show()
                        Toast.makeText(
                            context,
                            getString(R.string.toast_message_not_available_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.show()
        }
    }
}
