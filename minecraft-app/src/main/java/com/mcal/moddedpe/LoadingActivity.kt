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
import com.mcal.core.utils.ABIHelper.getABI
import com.mcal.core.utils.NetHelper
import com.mcal.moddedpe.adapters.DownloaderAdapter
import com.mcal.moddedpe.base.BaseActivity
import com.mcal.moddedpe.data.DownloadItem
import com.mcal.moddedpe.data.ResourceType
import com.mcal.moddedpe.databinding.ActivityLoadingPackBinding
import kotlinx.coroutines.*

class LoadingActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoadingPackBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val context = this
        if (installedBehaviorPack && installedMainPack && installedResourcePack && installedNative) {
            startActivity(Intent(context, GameActivity::class.java))
            finish()
        } else {
            runBlocking {
                notAvailableDialog()
            }
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
                        "https://github.com/TimScriptov/lokicraft/raw/main/moddedpe/$VERSION/resource_pack.zip",
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
                        "https://github.com/TimScriptov/lokicraft/raw/main/moddedpe/$VERSION/behavior_pack.zip",
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
                        "https://github.com/TimScriptov/lokicraft/raw/main/moddedpe/$VERSION/main_pack.zip",
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
                        "https://github.com/TimScriptov/lokicraft/raw/main/moddedpe/$VERSION/${getABI()}/libraries.zip",
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
                        if (installedResourcePack && installedBehaviorPack && installedMainPack && installedNative) {
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
    }

    private suspend fun notAvailableDialog() {
        val context = this@LoadingActivity
        if (!NetHelper.isNetworkAvailable(context)) {
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(context).apply {
                    setMessage(getString(R.string.dialog_message_not_available_internet))
                    setCancelable(false)
                    setPositiveButton(getString(R.string.btn_check_internet)) { d, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (NetHelper.isNetworkAvailable(context)) {
                                withContext(Dispatchers.Main) {
                                    d.dismiss()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    this@apply.show()
                                    Toast.makeText(
                                        context,
                                        getString(R.string.toast_message_not_available_internet),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }.show()
            }
        }
    }
}
