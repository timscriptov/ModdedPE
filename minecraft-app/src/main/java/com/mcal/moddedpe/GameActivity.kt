package com.mcal.moddedpe

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.moddedpe.task.CustomServers
import com.mcal.moddedpe.task.MapsInstaller
import com.mcal.moddedpe.task.NativeInstaller
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.Patcher
import com.mojang.minecraftpe.MainActivity
import java.io.File

class GameActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        CustomServers(this).install()
        MapsInstaller(this).install()
        NativeInstaller(this).install()
        patchNativeLibraryDir()
        loadLibraries()
        super.onCreate(savedInstanceState)
        showAgreeDialog()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (nativeKeyHandler(event.keyCode, event.action)) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                //
            }
        }
        return super.dispatchKeyEvent(event)
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
            MaterialAlertDialogBuilder(this).apply {
                setTitle("Title")
                setMessage(fromHtml("<body><p>Content</p></body>"))
                setCancelable(false)
                setNeutralButton("Privacy Policy") { _: DialogInterface, _: Int ->
                    startActivity(Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://localhost:1234")
                    ))
                }
                setPositiveButton("Agree") { dialogInterface: DialogInterface, _: Int ->
                    setAgreePrivacyPolice(false)
                    dialogInterface.dismiss()
                }
            }.show()
        }
    }

    private fun getPreferences(): SharedPreferences {
        return getSharedPreferences("preferences", 0)
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
