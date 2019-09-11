/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.utils.UtilsSettings
import com.mcal.pesdk.PreloadException
import com.mcal.pesdk.Preloader
import com.mcal.pesdk.nmod.NMod
import java.util.*

class PreloadActivity : BaseActivity() {
    private val mPreloadUIHandler = PreloadUIHandler()
    private var mPreloadingMessageLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_preloading)

        val tipsText = findViewById<AppCompatTextView>(R.id.moddedpe_preloading_text)
        val tipsArray = resources.getStringArray(R.array.preloading_tips_text)
        tipsText.text = tipsArray[Random().nextInt(tipsArray.size)]

        mPreloadingMessageLayout = findViewById(R.id.moddedpe_preloading_texts_adapted_layput)

        PreloadThread().start()
    }

    override fun onBackPressed() {

    }

    private fun writeNewText(text: String) {
        val message = Message()
        message.obj = text
        message.what = MSG_WRITE_TEXT
        mPreloadUIHandler.sendMessage(message)
    }

    private inner class PreloadThread : Thread() {
        private val mFailedNMods = ArrayList<NMod>()

        override fun run() {
            super.run()

            try {
                Preloader(peSdk, null, object : Preloader.PreloadListener() {
                    override fun onStart() {
                        writeNewText(getString(R.string.preloading_initing))
                        if (peSdk.launcherOptions.isSafeMode)
                            writeNewText(getString(R.string.preloading_initing_info_safe_mode, peSdk.minecraftInfo.getMinecraftVersionName()))
                        else
                            writeNewText(getString(R.string.preloading_initing_info, peSdk.nModAPI.versionName, peSdk.minecraftInfo.getMinecraftVersionName()))
                        try {
                            Thread.sleep(1500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onLoadNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs))
                    }

                    override fun onLoadSubstrateLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_substrate))
                    }

                    override fun onLoadFModLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_fmod))
                    }

                    override fun onLoadMinecraftPELib() {
                        writeNewText(getString(R.string.preloading_loading_lib_minecraftpe))
                    }

                    override fun onLoadPESdkLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_game_launcher))
                    }

                    override fun onFinishedLoadingNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs_done))
                    }

                    override fun onStartLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_start_loading))
                    }

                    override fun onFinishedLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_finish_loading))
                    }

                    override fun onNModLoaded(nmod: NMod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded, *arrayOf<Any>(nmod.packageName)))
                    }

                    override fun onFailedLoadingNMod(nmod: NMod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded_failed, *arrayOf<Any>(nmod.packageName)))
                        mFailedNMods.add(nmod)
                    }

                    override fun onFinish(bundle: Bundle) {
                        if (mFailedNMods.isEmpty()) {
                            writeNewText(getString(R.string.preloading_finished))
                            try {
                                Thread.sleep(1500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                            val message = Message()
                            message.what = MSG_START_MINECRAFT
                            message.data = bundle
                            mPreloadUIHandler.sendMessage(message)
                        } else {
                            val message = Message()
                            message.what = MSG_START_NMOD_LOADING_FAILED
                            message.obj = mFailedNMods
                            message.data = bundle
                            mPreloadUIHandler.sendMessage(message)
                        }
                    }

                }).preload(this@PreloadActivity)
            } catch (e: PreloadException) {
                val message = Message()
                message.what = MSG_ERROR
                message.obj = e
                mPreloadUIHandler.sendMessage(message)
            }

        }
    }

    private inner class PreloadUIHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == MSG_WRITE_TEXT) {
                val textView = layoutInflater.inflate(R.layout.moddedpe_ui_text_small, null) as AppCompatTextView
                textView.text = msg.obj as CharSequence
                mPreloadingMessageLayout!!.addView(textView)
            } else if (msg.what == MSG_START_MINECRAFT) {
                val intent = Intent(this@PreloadActivity, MinecraftActivity::class.java)
                intent.putExtras(msg.data)
                startActivity(intent)
                finish()
            } else if (msg.what == MSG_ERROR) {
                val preloadException = msg.obj as PreloadException
                UtilsSettings(this@PreloadActivity).openGameFailed = preloadException.toString()
                val intent = Intent(this@PreloadActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else if (msg.what == MSG_START_NMOD_LOADING_FAILED) {
                NModLoadFailActivity.startThisActivity(this@PreloadActivity, msg.obj as ArrayList<NMod>, msg.data)
                finish()
            }
        }
    }

    companion object {
        private val MSG_START_MINECRAFT = 1
        private val MSG_WRITE_TEXT = 2
        private val MSG_ERROR = 3
        private val MSG_START_NMOD_LOADING_FAILED = 4
    }
}
