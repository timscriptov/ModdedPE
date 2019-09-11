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
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import com.mcal.pesdk.utils.LauncherOptions
import java.util.*

class MCPkgPickerActivity : BaseActivity() {
    private val mUIHandler = UIHandler()
    private var mInstalledPackages: List<PackageInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pkg_picker)
        setResult(Activity.RESULT_CANCELED)
        setActionBarButtonCloseRight()

        val loading_view = findViewById<View>(R.id.pkg_picker_package_loading_view)
        loading_view.visibility = View.VISIBLE

        findViewById<View>(R.id.pkg_picker_reset_button).setOnClickListener { onResetClicked() }

        LoadingThread().start()
    }

    private fun showListView() {
        val loading_view = findViewById<View>(R.id.pkg_picker_package_loading_view)
        loading_view.visibility = View.GONE

        val list_view = findViewById<View>(R.id.pkg_picker_package_list_view)
        list_view.visibility = View.VISIBLE

        val list = list_view as ListView
        list.adapter = PackageListAdapter()
    }

    private fun showUnfoundView() {
        val loading_view = findViewById<View>(R.id.pkg_picker_package_loading_view)
        loading_view.visibility = View.GONE

        val view = findViewById<View>(R.id.pkg_picker_package_unfound_view)
        view.visibility = View.VISIBLE
    }

    private fun onResetClicked() {
        AlertDialog.Builder(this).setTitle(R.string.pick_tips_title).setMessage(R.string.pick_tips_reset_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
            p1.dismiss()
            val intent = Intent()
            val extras = Bundle()
            extras.putString(TAG_PACKAGE_NAME, LauncherOptions.STRING_VALUE_DEFAULT)
            intent.putExtras(extras)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
    }

    private inner class UIHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_SHOW_LIST_VIEW) {
                showListView()
            } else if (msg.what == MSG_SHOW_UNFOUND_VIEW) {
                showUnfoundView()
            }
        }
    }

    private inner class LoadingThread : Thread() {
        override fun run() {
            try {
                Thread.sleep(2500)
            } catch (e: InterruptedException) {
            }

            mInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_CONFIGURATIONS)
            if (mInstalledPackages != null && mInstalledPackages!!.isNotEmpty())
                mUIHandler.sendEmptyMessage(MSG_SHOW_LIST_VIEW)
            else
                mUIHandler.sendEmptyMessage(MSG_SHOW_UNFOUND_VIEW)
        }
    }

    private inner class PackageListAdapter : BaseAdapter() {
        init {
            Collections.sort(mInstalledPackages!!, object : Comparator<PackageInfo> {
                internal var pm = packageManager

                override fun compare(o1: PackageInfo, o2: PackageInfo): Int {
                    return pm.getApplicationLabel(o1.applicationInfo).toString().compareTo(pm.getApplicationLabel(o2.applicationInfo).toString(), ignoreCase = true)
                }
            })
        }

        override fun getCount(): Int {
            return mInstalledPackages!!.size
        }

        override fun getItem(p1: Int): Any {
            return p1
        }

        override fun getItemId(p1: Int): Long {
            return p1.toLong()
        }

        override fun getView(p1: Int, p2: View, p3: ViewGroup): View {
            val pkg = mInstalledPackages!![p1]
            val baseCardView = layoutInflater.inflate(R.layout.pkg_picker_item, null)
            val imageView = baseCardView.findViewById<AppCompatImageView>(R.id.pkg_picker_package_item_card_view_image_view)
            try {
                var appIcon: Bitmap? = BitmapFactory.decodeResource(createPackageContext(pkg.packageName, 0).resources, pkg.applicationInfo.icon)
                if (appIcon == null)
                    appIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
                imageView.setImageBitmap(appIcon)

            } catch (e: PackageManager.NameNotFoundException) {
            }

            val name = baseCardView.findViewById<AppCompatTextView>(R.id.pkg_picker_package_item_card_view_text_name)
            name.text = pkg.applicationInfo.loadLabel(packageManager)
            val pkgname = baseCardView.findViewById<AppCompatTextView>(R.id.pkg_picker_package_item_card_view_text_package_name)
            pkgname.text = pkg.packageName
            baseCardView.setOnClickListener {
                AlertDialog.Builder(this@MCPkgPickerActivity).setTitle(R.string.pick_tips_title).setMessage(getString(R.string.pick_tips_message, pkg.packageName, pkg.applicationInfo.loadLabel(packageManager))).setPositiveButton(android.R.string.ok) { p1, p2 ->
                    p1.dismiss()
                    val intent = Intent()
                    val extras = Bundle()
                    extras.putString(TAG_PACKAGE_NAME, pkg.packageName)
                    intent.putExtras(extras)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
            }
            return baseCardView
        }


    }

    companion object {
        const val TAG_PACKAGE_NAME = "package_name"
        const val REQUEST_PICK_PACKAGE = 5
        private const val MSG_SHOW_LIST_VIEW = 1
        private const val MSG_SHOW_UNFOUND_VIEW = 2

        fun startThisActivity(context: Activity) {
            val intent = Intent(context, MCPkgPickerActivity::class.java)
            context.startActivityForResult(intent, REQUEST_PICK_PACKAGE)
        }
    }
}
