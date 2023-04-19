package com.mcal.moddedpe

import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.core.AssetInstaller
import com.mcal.core.NativeInstaller
import com.mcal.core.utils.FileHelper.readFileAsLines
import com.mcal.core.utils.FileHelper.writeToFile
import com.mcal.core.utils.NetHelper.isNetworkAvailable
import com.mcal.moddedpe.base.BaseNativeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GameActivity : BaseNativeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        AssetInstaller(this).install()
        NativeInstaller(this).install()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        updateSettings()
        notAvailableDialog()
    }

    private fun notAvailableDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            val context = this@GameActivity
            if (!isNetworkAvailable(context)) {
                withContext(Dispatchers.Main) {
                    val dialog = MaterialAlertDialogBuilder(context)
                    dialog.setMessage(getString(R.string.dialog_message_not_available_internet))
                    dialog.setCancelable(false)
                    dialog.setPositiveButton(getString(R.string.btn_check_internet)) { d, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (isNetworkAvailable(context)) {
                                withContext(Dispatchers.Main) {
                                    d.dismiss()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    dialog.show()
                                    Toast.makeText(
                                        context,
                                        getString(R.string.toast_message_not_available_internet),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    dialog.show()
                }
            }
        }
    }

    private fun updateSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val minecraftPEDir = File(filesDir.parent, "games/com.mojang/minecraftpe/")
            val options = File(minecraftPEDir, "options.txt")
            if (options.exists()) {
                var content = ""
                for (line in readFileAsLines(options)) {
                    if (line.isEmpty()) {
                        continue
                    }
                    if (line.contains("new_edit_world_screen_beta")
                    ) {
                        content += "\nnew_edit_world_screen_beta:0"
                        continue
                    }
                    if (line.contains("new_play_screen_beta")) {
                        content += "\nnew_play_screen_beta:0"
                        continue
                    }
                    content += "\n" + line
                }
                writeToFile(options, content)
            } else {
                if (!minecraftPEDir.exists()) {
                    minecraftPEDir.mkdirs()
                }
                val content = "new_edit_world_screen_beta:0\nnew_play_screen_beta:0"
                writeToFile(options, content)
            }
            val treatmentsDir = File(filesDir.parent, "treatments")
            if (treatmentsDir.exists()) {
                treatmentsDir.deleteRecursively()
            }
        }
    }
}