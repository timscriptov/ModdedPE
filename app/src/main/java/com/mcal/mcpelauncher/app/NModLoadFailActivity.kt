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

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.mcal.mcpelauncher.R
import com.mcal.pesdk.nmod.NMod
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class NModLoadFailActivity : BaseActivity() {

    private var mPackageNames: ArrayList<String>? = ArrayList()
    private var mMessages: ArrayList<String>? = ArrayList()
    private var mTypeStrings: ArrayList<String>? = ArrayList()
    private var mIconPaths: ArrayList<String>? = ArrayList()
    private var mMCData: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_nmod_load_failed)

        mMessages = intent.extras!!.getStringArrayList(KEY_MESSAGE)
        mIconPaths = intent.extras!!.getStringArrayList(KEY_ICON_PATH)
        mTypeStrings = intent.extras!!.getStringArrayList(KEY_TYPE_STRING)
        mPackageNames = intent.extras!!.getStringArrayList(KEY_PACKAGE_NAME)
        mMCData = intent.extras!!.getBundle(KEY_MC_DATA)

        val errorListView = findViewById<ListView>(R.id.nmod_load_failed_list_view)
        errorListView.adapter = ViewAdapter()

        findViewById<View>(R.id.load_failed_next_button).setOnClickListener { onNextClicked() }
    }

    private fun onNextClicked() {
        val intent = Intent(this, MinecraftActivity::class.java)
        intent.putExtras(mMCData!!)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

    }

    private inner class ViewAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mPackageNames!!.size
        }

        override fun getItem(p1: Int): Any {
            return p1
        }

        override fun getItemId(p1: Int): Long {
            return p1.toLong()
        }

        @SuppressLint("StringFormatMatches")
        override fun getView(p1: Int, p2: View, p3: ViewGroup): View {
            val view = layoutInflater.inflate(R.layout.moddedpe_nmod_load_failed_item_card, null) as CardView
            val packageNameTextView = view.findViewById<AppCompatTextView>(R.id.moddedpe_nmod_load_failed_item_card_package_name)
            packageNameTextView.text = mPackageNames!![p1]
            val errorMessageTextView = view.findViewById<AppCompatTextView>(R.id.moddedpe_nmod_load_failed_item_card_message)
            errorMessageTextView.text = getString(R.string.load_fail_msg, mTypeStrings!![p1], mMessages!![p1])
            val imageViewIcon = view.findViewById<AppCompatImageView>(R.id.moddedpe_nmod_load_failed_item_card_icon)
            try {
                if (mIconPaths!![p1] != null)
                    imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(FileInputStream(mIconPaths!![p1])))
                else
                    imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack))
            } catch (e: FileNotFoundException) {
                imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack))
            }

            val index = p1
            view.setOnClickListener { AlertDialog.Builder(this@NModLoadFailActivity).setTitle(R.string.load_fail_title).setMessage(getString(R.string.load_fail_msg, *arrayOf<Any>(mTypeStrings!![index], mMessages!![index]))).setPositiveButton(android.R.string.ok) { p1, p2 -> p1.dismiss() }.show() }
            return view
        }
    }

    companion object {
        private val KEY_TYPE_STRING = "type_string"
        private val KEY_MESSAGE = "message"
        private val KEY_PACKAGE_NAME = "package_name"
        private val KEY_ICON_PATH = "icon_path"
        private val KEY_MC_DATA = "mc_data"

        fun startThisActivity(context: Context, nmods: ArrayList<NMod>, data: Bundle) {
            val intent = Intent(context, NModLoadFailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val bundle = Bundle()
            val mPackageNames = ArrayList<String>()
            val mMessages = ArrayList<String>()
            val mTypeStrings = ArrayList<String>()
            val mIconPaths = ArrayList<String>()
            for (nmod in nmods) {
                mPackageNames.add(nmod.packageName)
                mMessages.add(nmod.loadException?.cause.toString())
                mTypeStrings.add(nmod.loadException!!.toTypeString())
                val iconPath = nmod.copyIconToData()
                if (iconPath != null)
                    mIconPaths.add(iconPath.absolutePath)
                //SUKA
//                else
//                    mIconPaths.add(null)
            }
            bundle.putStringArrayList(KEY_MESSAGE, mMessages)
            bundle.putStringArrayList(KEY_ICON_PATH, mIconPaths)
            bundle.putStringArrayList(KEY_TYPE_STRING, mTypeStrings)
            bundle.putStringArrayList(KEY_PACKAGE_NAME, mPackageNames)
            bundle.putBundle(KEY_MC_DATA, data)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
