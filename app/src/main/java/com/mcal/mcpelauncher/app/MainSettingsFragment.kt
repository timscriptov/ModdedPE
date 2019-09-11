/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcal.mcpelauncher.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.utils.I18n
import com.mcal.mcpelauncher.utils.UtilsSettings
import com.mcal.pesdk.utils.LauncherOptions
import org.zeroturnaround.zip.ZipUtil
import org.zeroturnaround.zip.commons.FileUtils
import java.io.*

class MainSettingsFragment : PreferenceFragmentCompat() {
    private lateinit var mSettings: UtilsSettings
    private lateinit var mDataPathPreference: Preference
    private lateinit var mPkgPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.preferences)

        mSettings = UtilsSettings(activity!!)

        val mBackgroundMusicPreference = findPreference("background_music") as SwitchPreference
        mBackgroundMusicPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p1, p2 ->
            if (p2 as Boolean) {
                (activity as BackgroundSoundPlayer).bind()
            } else
                (activity as BackgroundSoundPlayer).unbind()
            true
        }

        val mDesktopGuiPreference = findPreference("desktop_gui") as SwitchPreference
        mDesktopGuiPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p1, p2 ->
            val optionsFileDir = File(Environment.getExternalStorageDirectory().toString() + "/games/com.mojang/minecraftpe")
            val optionsFile = File("$optionsFileDir/options.txt")

            if (!optionsFileDir.exists()) {
                optionsFileDir.mkdirs()
            }

            if (!optionsFile.exists()) {
                try {
                    val writer = FileWriter(optionsFile)
                    writer.write("gfx_guiscale_offset:0")
                    writer.flush()
                } catch (ignored: IOException) {
                }
            }

            var fileContent: String? = null
            try {
                fileContent = FileUtils.readFileToString(optionsFile)
            } catch (ignored: IOException) {
            }

            if (p2 as Boolean) {
                try {
                    val writer = FileWriter(optionsFile)
                    writer.write(fileContent!!.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)".toRegex(), "gfx_guiscale_offset:-1"))
                    writer.flush()
                } catch (e: IOException) {
                }

            } else {
                try {
                    val writer = FileWriter(optionsFile)
                    writer.write(fileContent!!.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)".toRegex(), "gfx_guiscale_offset:0"))
                    writer.flush()
                } catch (e: IOException) {
                }
            }
            true
        }

        val mModdedPEPackPreference = findPreference("moddedpe_pack") as SwitchPreference
        mModdedPEPackPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p1, p2 ->
            val dir = File(Environment.getExternalStorageDirectory().toString() + "/games/com.mojang/resource_packs/ModdedPE")
            val gsonFileDir = File(Environment.getExternalStorageDirectory().toString() + "/games/com.mojang/minecraftpe")
            val gsonFile = File(Environment.getExternalStorageDirectory().toString() + "/games/com.mojang/minecraftpe/global_resource_packs.json")

            if (p2 as Boolean) {
                if (!dir.exists()) {
                    dir.mkdirs()
                    try {
                        ZipUtil.unpack(activity!!.assets.open("ModdedPE.zip"), dir)
                    } catch (e: IOException) {
                    }

                }

                gsonFileDir.mkdirs()
                try {
                    val `is` = activity!!.assets.open("global_resource_packs.json")
                    val fw = FileWriter("$gsonFileDir/global_resource_packs.json")
                    val isr = InputStreamReader(BufferedInputStream(`is`))
                    val br = BufferedReader(isr)

                    var line: String? = null
                    while ({ line = br.readLine(); line }() != null) {
                        fw.write(line + "\n")
                    }
                    fw.flush()
                    fw.close()
                    br.close()
                } catch (e: IOException) {
                }

            } else {
                try {
                    FileUtils.deleteDirectory(dir)
                    gsonFile.delete()
                } catch (e: IOException) {
                }
            }
            true
        }

        val mSafeModePreference = findPreference("safe_mode") as SwitchPreference
        mSafeModePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p1, p2 ->
            mSettings.isSafeMode = p2 as Boolean
            true
        }
        mSafeModePreference.isChecked = mSettings.isSafeMode

        mDataPathPreference = findPreference("data_saved_path")
        mDataPathPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (checkPermissions())
                DirPickerActivity.startThisActivity(activity!!)
            true
        }
        val mAboutPreference = findPreference("about")
        mAboutPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            activity!!.startActivity(intent)
            true
        }
        mPkgPreference = findPreference("game_pkg_name")
        mPkgPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            MCPkgPickerActivity.startThisActivity(activity!!)
            true
        }

        val mLanguagePreference = findPreference("language") as ListPreference
        mLanguagePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p1, p2 ->
            val type = Integer.valueOf(p2 as String)
            mSettings.languageType = type
            I18n.setLanguage(activity)
            val intent = Intent(activity, SplashesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            true
        }
        updatePreferences()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DirPickerActivity.REQUEST_PICK_DIR && resultCode == Activity.RESULT_OK) {
            val dir = data!!.extras!!.getString(DirPickerActivity.TAG_DIR_PATH)
            mSettings!!.dataSavedPath = dir!!
            if (dir == LauncherOptions.STRING_VALUE_DEFAULT)
                Snackbar.make(activity!!.window.decorView, getString(R.string.preferences_update_message_reset_data_path), 2500).show()
            else
                Snackbar.make(activity!!.window.decorView, getString(R.string.preferences_update_message_data_path, *arrayOf<Any>(dir)), 2500).show()
        } else if (requestCode == MCPkgPickerActivity.REQUEST_PICK_PACKAGE && resultCode == Activity.RESULT_OK) {
            val pkgName = data!!.extras!!.getString(MCPkgPickerActivity.TAG_PACKAGE_NAME)
            mSettings!!.setMinecraftPackageName(pkgName!!)
            if (pkgName == LauncherOptions.STRING_VALUE_DEFAULT)
                Snackbar.make(activity!!.window.decorView, getString(R.string.preferences_update_message_reset_pkg_name), 2500).show()
            else
                Snackbar.make(activity!!.window.decorView, getString(R.string.preferences_update_message_pkg_name, *arrayOf<Any>(pkgName)), 2500).show()
            val intent = Intent(activity, SplashesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            activity!!.startActivity(intent)
        }
        updatePreferences()
    }

    private fun updatePreferences() {
        if (mSettings.dataSavedPath != LauncherOptions.STRING_VALUE_DEFAULT)
            mDataPathPreference.summary = mSettings.dataSavedPath
        else
            mDataPathPreference.setSummary(R.string.preferences_summary_data_saved_path)

        if (mSettings.minecraftPEPackageName != LauncherOptions.STRING_VALUE_DEFAULT)
            mPkgPreference.summary = mSettings.minecraftPEPackageName
        else
            mPkgPreference.setSummary(R.string.preferences_summary_game_pkg_name)
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
    }

    private fun showPermissionDinedDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.permission_grant_failed_title)
        builder.setMessage(R.string.permission_grant_failed_message)
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + activity!!.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            var isAllGranted = true

            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false
                    break
                }
            }

            if (isAllGranted) {
                DirPickerActivity.startThisActivity(activity!!)
            } else {
                showPermissionDinedDialog()
            }
        }
    }
}
