package com.mcal.moddedpe

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mcal.core.AssetInstaller
import com.mcal.core.NativeInstaller
import com.mcal.core.data.StorageHelper.minecraftPEDir
import com.mcal.core.utils.FileHelper.readFileAsLines
import com.mcal.core.utils.FileHelper.writeToFile
import com.mcal.moddedpe.base.BaseNativeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    }

    private fun updateSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val minecraftPEDir = minecraftPEDir(this@GameActivity)
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