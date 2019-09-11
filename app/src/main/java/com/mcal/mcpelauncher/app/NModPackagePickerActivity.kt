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

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.mcal.mcpelauncher.R
import com.mcal.pesdk.nmod.NMod
import java.util.*

class NModPackagePickerActivity : BaseActivity() {
    private val mUIHandler = UIHandler()
    private var nmods = ArrayList<NMod>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nmod_picker_package)
        setResult(Activity.RESULT_CANCELED)
        setActionBarButtonCloseRight()

        val loading_view = findViewById<View>(R.id.nmod_picker_package_loading_view)
        loading_view.visibility = View.VISIBLE

        LoadingThread().start()
    }

    private fun showListView() {
        val loading_view = findViewById<View>(R.id.nmod_picker_package_loading_view)
        loading_view.visibility = View.GONE

        val list_view = findViewById<View>(R.id.nmod_picker_package_list_view)
        list_view.visibility = View.VISIBLE

        val list = list_view as ListView
        list.adapter = PackageListAdapter()
    }

    private fun showUnFoundView() {
        val loading_view = findViewById<View>(R.id.nmod_picker_package_loading_view)
        loading_view.visibility = View.GONE

        val view = findViewById<View>(R.id.nmod_picker_package_unfound_view)
        view.visibility = View.VISIBLE
    }

    private inner class UIHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_SHOW_LIST_VIEW) {
                showListView()
            } else if (msg.what == MSG_SHOW_UNFOUND_VIEW) {
                showUnFoundView()
            }
        }
    }

    private inner class LoadingThread : Thread() {
        override fun run() {
            nmods = peSdk.nModAPI.findInstalledNMods()
            if (nmods.size > 0)
                mUIHandler.sendEmptyMessage(MSG_SHOW_LIST_VIEW)
            else
                mUIHandler.sendEmptyMessage(MSG_SHOW_UNFOUND_VIEW)
        }
    }

    private inner class PackageListAdapter : BaseAdapter() {
        init {
            nmods.sortWith(Comparator { o1, o2 -> o1.name!!.compareTo(o2.name!!, ignoreCase = true) })
        }

        override fun getCount(): Int {
            return nmods.size
        }

        override fun getItem(p1: Int): Any {
            return p1
        }

        override fun getItemId(p1: Int): Long {
            return p1.toLong()
        }

        override fun getView(p1: Int, p2: View, p3: ViewGroup): View {
            val nmod = nmods[p1]
            val baseCardView = layoutInflater.inflate(R.layout.nmod_picker_package_item, null)
            val imageView = baseCardView.findViewById<AppCompatImageView>(R.id.nmod_picker_package_item_card_view_image_view)
            var nmodIcon: Bitmap? = nmod.icon
            if (nmodIcon == null)
                nmodIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
            imageView.setImageBitmap(nmodIcon)
            val name = baseCardView.findViewById<AppCompatTextView>(R.id.nmod_picker_package_item_card_view_text_name)
            name.text = nmod.name
            val pkgname = baseCardView.findViewById<AppCompatTextView>(R.id.nmod_picker_package_item_card_view_text_package_name)
            pkgname.text = nmod.packageName
            baseCardView.setOnClickListener {
                val intent = Intent()
                val extras = Bundle()
                extras.putString(TAG_PACKAGE_NAME, nmod.packageName)
                intent.putExtras(extras)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            baseCardView.setOnLongClickListener {
                NModDescriptionActivity.startThisActivity(this@NModPackagePickerActivity, nmod)
                false
            }
            return baseCardView
        }


    }

    companion object {
        val TAG_PACKAGE_NAME = "package_name"
        val REQUEST_PICK_PACKAGE = 1
        private val MSG_SHOW_LIST_VIEW = 1
        private val MSG_SHOW_UNFOUND_VIEW = 2

        fun startThisActivity(context: Activity) {
            val intent = Intent(context, NModPackagePickerActivity::class.java)
            context.startActivityForResult(intent, REQUEST_PICK_PACKAGE)
        }
    }
}
