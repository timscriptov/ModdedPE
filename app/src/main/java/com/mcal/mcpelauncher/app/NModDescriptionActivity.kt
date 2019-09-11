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

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.mcal.mcpelauncher.R
import com.mcal.pesdk.nmod.NMod
import java.io.FileInputStream

class NModDescriptionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_nmod_description)

        val nmodPackageName = intent.extras!!.getString(TAG_PACKAGE_NAME)
        var icon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
        try {
            val iconpath = intent.extras!!.getString(TAG_ICON_PATH)
            val fileInput = FileInputStream(iconpath!!)
            icon = BitmapFactory.decodeStream(fileInput)
        } catch (e: Throwable) {
        }

        val description = intent.extras!!.getString(TAG_DESCRIPTION)
        val name = intent.extras!!.getString(TAG_NAME)
        val version_name = intent.extras!!.getString(TAG_VERSION_NAME)
        val author = intent.extras!!.getString(TAG_AUTHOR)
        val change_log = intent.extras!!.getString(TAG_CHANGE_LOG)
        val minecraft_version_name = intent.extras!!.getString(TAG_MINECRAFT_VERSION_NAME)

        setTitle(name!!)
        setActionBarButtonCloseRight()

        val iconImage = findViewById<View>(R.id.moddedpenmoddescriptionImageViewIcon) as AppCompatImageView
        iconImage.setImageBitmap(icon)

        val textViewName = findViewById<View>(R.id.moddedpenmoddescriptionTextViewNModName) as AppCompatTextView
        textViewName.text = name
        val textViewPackageName = findViewById<View>(R.id.moddedpenmoddescriptionTextViewNModPackageName) as AppCompatTextView
        textViewPackageName.text = nmodPackageName
        val textViewDescription = findViewById<View>(R.id.moddedpenmoddescriptionTextViewDescription) as AppCompatTextView
        textViewDescription.text = description ?: getString(R.string.nmod_description_unknow)
        val textViewAuthor = findViewById<View>(R.id.moddedpenmoddescriptionTextViewAuthor) as AppCompatTextView
        textViewAuthor.text = author ?: getString(R.string.nmod_description_unknow)
        val textViewVersionName = findViewById<View>(R.id.moddedpenmoddescriptionTextViewVersionName) as AppCompatTextView
        textViewVersionName.text = version_name ?: getString(R.string.nmod_description_unknow)
        val textViewWhatsNew = findViewById<View>(R.id.moddedpenmoddescriptionTextViewWhatsNew) as AppCompatTextView
        textViewWhatsNew.text = change_log ?: getString(R.string.nmod_description_unknow)
        val textViewMinecraftVersionName = findViewById<View>(R.id.moddedpenmoddescriptionTextViewMinecraftVersionName) as AppCompatTextView
        textViewMinecraftVersionName.text = minecraft_version_name
                ?: getString(R.string.nmod_description_unknow)
    }

    companion object {
        val TAG_PACKAGE_NAME = "nmod_package_name"
        val TAG_NAME = "nmod_name"
        val TAG_AUTHOR = "author"
        val TAG_VERSION_NAME = "version_name"
        val TAG_DESCRIPTION = "description"
        val TAG_ICON_PATH = "icon_path"
        val TAG_CHANGE_LOG = "change_log"
        val TAG_MINECRAFT_VERSION_NAME = "minecraft_version_name"

        fun startThisActivity(context: Context, nmod: NMod) {
            val intent = Intent(context, NModDescriptionActivity::class.java)
            val bundle = Bundle()
            bundle.putString(TAG_PACKAGE_NAME, nmod.packageName)
            bundle.putString(TAG_NAME, nmod.name)
            bundle.putString(TAG_DESCRIPTION, nmod.description)
            bundle.putString(TAG_AUTHOR, nmod.author)
            bundle.putString(TAG_VERSION_NAME, nmod.versionName)
            bundle.putString(TAG_CHANGE_LOG, nmod.changeLog)
            bundle.putString(TAG_MINECRAFT_VERSION_NAME, nmod.minecraftVersionName)
            val iconPath = nmod.copyIconToData()
            if (iconPath != null)
                bundle.putString(TAG_ICON_PATH, iconPath.absolutePath)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
