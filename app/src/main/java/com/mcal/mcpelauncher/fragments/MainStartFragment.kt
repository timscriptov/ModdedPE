/*
 * Copyright (C) 2018-2021 Тимашков Иван
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
package com.mcal.mcpelauncher.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.mcal.mcpelauncher.BuildConfig
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.PreloadActivity
import com.mcal.mcpelauncher.data.Preferences
import com.mcal.mcpelauncher.ui.AboutActivity

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class MainStartFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.moddedpe_main, null)
        view.findViewById<View>(R.id.moddedpe_main_play_button).setOnClickListener { onPlayClicked() }
        view.findViewById<View>(R.id.moddedpe_main_about_button).setOnClickListener { onAboutClicked() }
        return view
    }

    private fun onAboutClicked() {
        val intent = Intent(activity, AboutActivity::class.java)
        requireActivity().startActivity(intent)
    }

    private fun onPlayClicked() {
        if (!ModdedPEApplication.mPESdk.minecraftInfo.isMinecraftInstalled) {
            val mdialog = AlertDialog.Builder(requireActivity())
            mdialog.setTitle(getString(R.string.no_mcpe_found_title))
            mdialog.setMessage(getString(R.string.no_mcpe_found))
            mdialog.setPositiveButton(getString(android.R.string.cancel)) { p1: DialogInterface, _: Int -> p1.dismiss() }
            mdialog.show()
        } else if (!ModdedPEApplication.mPESdk.minecraftInfo.isSupportedMinecraftVersion(resources.getStringArray(R.array.target_mcpe_versions))) {
            val mdialog = AlertDialog.Builder(requireActivity())
            mdialog.setTitle(getString(R.string.no_available_mcpe_version_found_title))
            mdialog.setMessage(getString(R.string.no_available_mcpe_version_found, ModdedPEApplication.mPESdk.minecraftInfo.minecraftVersionName, R.string.app_game.toString() + " " + BuildConfig.VERSION_NAME))
            mdialog.setNegativeButton(getString(android.R.string.cancel)) { p1: DialogInterface, _: Int -> p1.dismiss() }
            mdialog.setPositiveButton(getString(R.string.no_available_mcpe_version_continue)) { _: DialogInterface?, _: Int -> startMinecraft() }
            mdialog.show()
        } else startMinecraft()
    }

    private fun startMinecraft() {
        if (Preferences.isSafeMode) {
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.setTitle(R.string.safe_mode_on_title)
            dialog.setMessage(R.string.safe_mode_on_message)
            dialog.setPositiveButton(android.R.string.ok) { p1: DialogInterface, p2: Int ->
                val intent = Intent(activity, PreloadActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                p1.dismiss()
            }
            dialog.setNegativeButton(android.R.string.cancel) { p1: DialogInterface, _: Int -> p1.dismiss() }
            dialog.show()
        } else {
            startActivity(Intent(activity, PreloadActivity::class.java))
            requireActivity().finish()
        }
    }
}