package com.mcal.moddedpe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.core.data.StorageHelper.VERSION
import com.mcal.core.data.StorageHelper.behaviorPackFile
import com.mcal.core.data.StorageHelper.getNativeLibrariesFile
import com.mcal.core.data.StorageHelper.mainPackFile
import com.mcal.core.data.StorageHelper.resourcePackFile
import com.mcal.core.data.StorageHelper.vanillaResourcePackFile
import com.mcal.core.utils.ABIHelper.getABI
import com.mcal.core.utils.NetHelper
import com.mcal.moddedpe.adapters.DownloaderAdapter
import com.mcal.moddedpe.base.BaseActivity
import com.mcal.moddedpe.data.DownloadItem
import com.mcal.moddedpe.data.ResourceType
import com.mcal.moddedpe.databinding.ActivityLoadingPackBinding
import kotlinx.coroutines.*

class LoadingActivity : BaseActivity() {
    private val LINK_RESOURCE_PACK =
        "https://github.com/TimScriptov/lokicraft/raw/main/$VERSION/resource_pack.zip"
    private val LINK_VANILLA_RESOURCE_PACK =
        "https://github.com/TimScriptov/lokicraft/raw/main/$VERSION/resource_pack_vanilla.zip"
    private val LINK_BEHAVIOR_PACK =
        "https://github.com/TimScriptov/lokicraft/raw/main/$VERSION/behavior_pack.zip"
    private val LINK_MAIN_PACK =
        "https://github.com/TimScriptov/lokicraft/raw/main/$VERSION/main_pack.zip"
    private val LINK_LIBRARIES =
        "https://github.com/TimScriptov/lokicraft/raw/main/$VERSION/${getABI()}/libraries.zip"

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoadingPackBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val context = this
        if (installedResourcePack && installedBehaviorPack && installedMainPack && installedVanillaResourcePack) {
            startActivity(Intent(context, GameActivity::class.java))
            finish()
        } else {
            val list = mutableListOf<DownloadItem>()
            if (!installedResourcePack) {
                val resourcePackFile = resourcePackFile(this)
                if (resourcePackFile.exists()) {
                    resourcePackFile.delete()
                }
                list.add(
                    DownloadItem(
                        ResourceType.RESOURCE,
                        "Downloading resource pack",
                        LINK_RESOURCE_PACK,
                        resourcePackFile,
                    )
                )
            }
            if (!installedBehaviorPack) {
                val behaviorPackFile = behaviorPackFile(this)
                if (behaviorPackFile.exists()) {
                    behaviorPackFile.delete()
                }
                list.add(
                    DownloadItem(
                        ResourceType.BEHAVIOR,
                        "Downloading behavior pack",
                        LINK_BEHAVIOR_PACK,
                        behaviorPackFile,
                    )
                )
            }
            if (!installedMainPack) {
                val mainPackFile = mainPackFile(this)
                if (mainPackFile.exists()) {
                    mainPackFile.delete()
                }
                list.add(
                    DownloadItem(
                        ResourceType.MAIN,
                        "Downloading main pack",
                        LINK_MAIN_PACK,
                        mainPackFile,
                    )
                )
            }
            if (!installedNative) {
                val nativeLibrariesFile = getNativeLibrariesFile(this)
                if (nativeLibrariesFile.exists()) {
                    nativeLibrariesFile.delete()
                }
                list.add(
                    DownloadItem(
                        ResourceType.LIBS,
                        "Downloading dynamical libraries",
                        LINK_LIBRARIES,
                        nativeLibrariesFile,
                    )
                )
            }
            if (!installedVanillaResourcePack) {
                val nativeLibrariesFile = getNativeLibrariesFile(this)
                if (nativeLibrariesFile.exists()) {
                    nativeLibrariesFile.delete()
                }
                list.add(
                    DownloadItem(
                        ResourceType.VANILLA,
                        "Downloading textures pack",
                        LINK_LIBRARIES,
                        nativeLibrariesFile,
                    )
                )
            }
            binding.recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = DownloaderAdapter(
                    context,
                    list
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                repeat(Int.MAX_VALUE) {
                    withContext(Dispatchers.Main) {
                        if (installedResourcePack && installedBehaviorPack && installedMainPack && installedVanillaResourcePack) {
                            startActivity(
                                Intent(
                                    context,
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
        if (!installedNative) {
            val nativeLibrariesFile = getNativeLibrariesFile(this)
            if (nativeLibrariesFile.exists()) {
                nativeLibrariesFile.delete()
            }
        }
        if (!installedVanillaResourcePack) {
            val vanillaResourcePackFile = vanillaResourcePackFile(this)
            if (vanillaResourcePackFile.exists()) {
                vanillaResourcePackFile.delete()
            }
        }
    }
}
