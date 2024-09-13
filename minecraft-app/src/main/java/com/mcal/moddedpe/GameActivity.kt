package com.mcal.moddedpe

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.moddedpe.App.Companion.PRIVACY_POLICE_GAME
import com.mcal.moddedpe.App.Companion.PRIVACY_POLICE_MINECRAFT
import com.mcal.moddedpe.App.Companion.PRIVACY_POLICE_XBOX
import com.mcal.moddedpe.ads.wortise.WortiseAdActivity
import com.mcal.moddedpe.task.ResourceInstaller
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.Patcher
import kotlinx.coroutines.runBlocking
import java.io.File

class GameActivity : WortiseAdActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking {
            ResourceInstaller.install(this@GameActivity)
            runOnUiThread {
                patchNativeLibraryDir()
                loadLibraries()
                super.onCreate(savedInstanceState)
                showAgreeDialog()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun patchNativeLibraryDir() {
        runCatching {
            Patcher.patchNativeLibraryDir(classLoader, File("${filesDir}/native/${ABIHelper.getABI()}/"))
        }
    }

    private fun loadLibraries() {
        System.loadLibrary("fmod")
        arrayListOf(
            "c++_shared",
            "minecraftpe",
            "MediaDecoders_Android"
        ).forEach {
            runCatching {
                System.loadLibrary(it)
            }
        }
    }

    override fun launchUri(uri: String?) {
        if (uri != null) {
            println(uri)
            if (App.REDIRECT_MINECRAFT_URLS.any { it.startsWith(uri) }) {
                super.launchUri(App.REDIRECT_URL)
                return
            }
        }
        super.launchUri(uri)
    }

    private fun showAgreeDialog() {
        if (isAgreePrivacyPolice()) {
            MaterialAlertDialogBuilder(this).apply {
                setTitle("${getString(R.string.app_name)} Privacy Policy")
                setMessage(
                    fromHtml(
                        "<p>Xbox Privacy Policy</p>" +
                                "<p>ModdedPE collects account information to allow autocompleting Xbox Login details,and the information is only used to login Xbox.</p>" +
                                "<p>The Xbox Login functionality's privacy policy can be found at <a href=\"$PRIVACY_POLICE_XBOX\">$PRIVACY_POLICE_XBOX</a></p>" +
                                "<br>" +
                                "<p>MinecraftPE Privacy Policy</p>" +
                                "<p>Some anonymous information is collected and sent to Mojang to improve MinecraftPE.</p>" +
                                "<p>Mojang's Privacy Policy can be found at <a href=\"$PRIVACY_POLICE_MINECRAFT\">$PRIVACY_POLICE_MINECRAFT</a></p></body>"
                    )
                )
                setCancelable(false)
                setNeutralButton("Privacy Policy") { _: DialogInterface, _: Int ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICE_GAME)))
                    show()
                }
                setPositiveButton("Agree") { dialogInterface: DialogInterface, _: Int ->
                    setAgreePrivacyPolice(false)
                    dialogInterface.dismiss()
                }
            }.show()
        }
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun isAgreePrivacyPolice(): Boolean {
        return getPreferences().getBoolean("privacyAcc", true)
    }

    private fun setAgreePrivacyPolice(mode: Boolean) {
        val editor = getPreferences().edit()
        editor.putBoolean("privacyAcc", mode)
        editor.apply()
    }

    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }
}
