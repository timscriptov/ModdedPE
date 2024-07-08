package com.mcal.moddedpe

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.moddedpe.task.CustomServers
import com.mcal.moddedpe.task.MapsInstaller
import com.mcal.moddedpe.task.NativeInstaller
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.Patcher
import java.io.File

class GameActivity : AdActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        CustomServers(this).install()
        MapsInstaller(this).install()
        NativeInstaller(this).install()
        patchNativeLibraryDir()
        loadLibraries()
        super.onCreate(savedInstanceState)
        showAgreeDialog()
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

    private fun showAgreeDialog() {
        if (isAgreePrivacyPolice()) {
            val gamePrivacyPolice = "https://github.com/timscriptov/ModdedPE/tree/master/PrivacyPolicy/README.md"
            val xboxPrivacyPolice = "https://privacy.microsoft.com/en-us/privacystatement"
            val minecraftPrivacyPolice = "https://account.mojang.com/terms#privacy"
            MaterialAlertDialogBuilder(this).apply {
                setTitle("ModdedPE Privacy Policy")
                setMessage(
                    fromHtml(
                        "<p>Xbox Privacy Policy</p>" +
                                "<p>ModdedPE collects account information to allow autocompleting Xbox Login details,and the information is only used to login Xbox.</p>" +
                                "<p>The Xbox Login functionality's privacy policy can be found at <a href=\"$xboxPrivacyPolice\">$xboxPrivacyPolice</a></p>" +
                                "<br>" +
                                "<p>MinecraftPE Privacy Policy</p>" +
                                "<p>Some anonymous information is collected and sent to Mojang to improve MinecraftPE.</p>" +
                                "<p>Mojang's Privacy Policy can be found at <a href=\"$minecraftPrivacyPolice\">$minecraftPrivacyPolice</a></p></body>"
                    )
                )
                setCancelable(false)
                setNeutralButton("Privacy Policy") { _: DialogInterface, _: Int ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(gamePrivacyPolice)))
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
