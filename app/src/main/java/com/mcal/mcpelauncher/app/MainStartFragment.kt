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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.utils.UtilsSettings

class MainStartFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.moddedpe_main, null)
        view.findViewById<View>(R.id.moddedpe_main_play_button).setOnClickListener { onPlayClicked() }
        return view
    }

    private fun onPlayClicked() {
        if (!peSdk.minecraftInfo.isMinecraftInstalled) {
            val mdialog = AlertDialog.Builder(activity!!)
            mdialog.setTitle(getString(R.string.no_mcpe_found_title))
            mdialog.setMessage(getString(R.string.no_mcpe_found))
            mdialog.setPositiveButton(getString(android.R.string.cancel)) { p1, _ -> p1.dismiss() }
            mdialog.show()
        } else if (!peSdk.minecraftInfo.isSupportedMinecraftVersion(resources.getStringArray(R.array.target_mcpe_versions))) {
            val mdialog = AlertDialog.Builder(activity!!)
            mdialog.setTitle(getString(R.string.no_available_mcpe_version_found_title))
            mdialog.setMessage(getString(R.string.no_available_mcpe_version_found, peSdk.minecraftInfo.minecraftVersionName, getString(R.string.target_mcpe_version_info)))
            mdialog.setNegativeButton(getString(android.R.string.cancel)) { p1, id -> p1.dismiss() }
            mdialog.setPositiveButton(getString(R.string.no_available_mcpe_version_continue)) { p1, id -> startMinecraft() }
            mdialog.show()
        } else
            startMinecraft()
    }

    private fun startMinecraft() {
        if (UtilsSettings(activity!!).isSafeMode) {
            AlertDialog.Builder(activity!!).setTitle(R.string.safe_mode_on_title).setMessage(R.string.safe_mode_on_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
                val intent = Intent(activity, PreloadActivity::class.java)
                startActivity(intent)
                activity!!.finish()
                p1.dismiss()
            }.setNegativeButton(android.R.string.cancel) { p1, _ -> p1.dismiss() }.show()
        } else {
            startActivity(Intent(activity, PreloadActivity::class.java))
            activity!!.finish()
        }
    }
}
