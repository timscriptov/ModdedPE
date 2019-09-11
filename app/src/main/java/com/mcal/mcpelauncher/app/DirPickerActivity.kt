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
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.mcal.mcpelauncher.R
import com.mcal.pesdk.utils.LauncherOptions
import java.io.File
import java.util.*

class DirPickerActivity : BaseActivity() {
    private var currentPath: File? = null
    private var filesInCurrentPath: ArrayList<File>? = null
    private val mSelectHandler = SelectHandler()

    private val isValidParent: Boolean
        get() = currentPath!!.parentFile != null && currentPath!!.parentFile!!.exists() && currentPath!!.parentFile!!.listFiles() != null && currentPath!!.parentFile!!.listFiles()!!.size > 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_dir_picker)

        findViewById<View>(R.id.moddedpe_dir_picker_fab_reset).setOnClickListener { onResetClicked() }
        findViewById<View>(R.id.moddedpe_dir_picker_fab_select).setOnClickListener { onSelectThisClicked() }

        setResult(Activity.RESULT_CANCELED, Intent())
        setActionBarButtonCloseRight()

        var pathString: String? = null
        try {
            pathString = intent.extras!!.getString(TAG_DIR_PATH)
        } catch (t: Throwable) {
        }

        if (pathString == null)
            pathString = Environment.getExternalStorageDirectory().absolutePath
        currentPath = File(pathString!!)

        openDirectory(currentPath)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun onSelectThisClicked() {
        val data = Intent()
        val extras = Bundle()
        extras.putString(TAG_DIR_PATH, currentPath!!.absolutePath)
        data.putExtras(extras)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun onResetClicked() {
        AlertDialog.Builder(this).setTitle(R.string.dir_picker_reset_warning_title).setMessage(R.string.dir_picker_reset_warning_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
            p1.dismiss()
            val data = Intent()
            val extras = Bundle()
            extras.putString(TAG_DIR_PATH, LauncherOptions.STRING_VALUE_DEFAULT)
            data.putExtras(extras)
            setResult(Activity.RESULT_OK, data)
            finish()
        }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
    }

    private fun select(file_arg: File?) {
        object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(450)
                } catch (e: InterruptedException) {
                }

                val mMessage = Message()
                mMessage.what = MSG_SELECT
                mMessage.obj = file_arg
                mSelectHandler.sendMessage(mMessage)
            }
        }.start()
    }

    private fun openDirectory(directory: File?) {
        currentPath = directory
        filesInCurrentPath = ArrayList()

        val unmanagedFilesInCurrentDirectory = currentPath!!.listFiles()
        if (unmanagedFilesInCurrentDirectory != null) {
            for (fileItem in unmanagedFilesInCurrentDirectory) {
                if (fileItem.isDirectory)
                    filesInCurrentPath!!.add(fileItem)
            }
        }

        filesInCurrentPath!!.sortWith(Comparator { o1, o2 ->
            when {
                o1.isDirectory and o2.isFile -> -1
                o1.isFile and o2.isDirectory -> 1
                else -> o1.name.compareTo(o2.name, ignoreCase = true)
            }
        })

        val fileListView = findViewById<View>(R.id.picker_dir_list_view) as ListView
        fileListView.adapter = FileAdapter()
    }

    private inner class FileAdapter : BaseAdapter() {
        override fun getCount(): Int {
            if (isValidParent)
                return filesInCurrentPath!!.size + 1
            return if (filesInCurrentPath!!.size == 0) 1 else filesInCurrentPath!!.size
        }

        override fun getItem(p1: Int): Any {
            return p1
        }

        override fun getItemId(p1: Int): Long {
            return p1.toLong()
        }

        override fun getView(p1: Int, p2: View, p3: ViewGroup): View {
            var p1 = p1
            val cardView = layoutInflater.inflate(R.layout.nmod_picker_file_item, null) as CardView

            if (!currentPath!!.path.endsWith(File.separator)) {
                if (p1 == 0) {
                    val fileImage = cardView.findViewById<AppCompatImageView>(R.id.nmod_picker_item_card_view_image_view)
                    fileImage.setImageResource(R.drawable.ic_folder_outline)

                    val textFileName = cardView.findViewById<AppCompatTextView>(R.id.nmod_picker_item_card_view_text_name)
                    textFileName.text = "..."

                    cardView.setOnClickListener { select(null) }
                } else {
                    val currentCardViewFile = filesInCurrentPath!![--p1]
                    val fileImage = cardView.findViewById<AppCompatImageView>(R.id.nmod_picker_item_card_view_image_view)
                    fileImage.setImageResource(R.drawable.ic_folder)

                    val textFileName = cardView.findViewById<AppCompatTextView>(R.id.nmod_picker_item_card_view_text_name)
                    textFileName.text = currentCardViewFile.name

                    cardView.setOnClickListener { select(currentCardViewFile) }
                }
            } else {
                val currentCardViewFile = filesInCurrentPath!![p1]
                val fileImage = cardView.findViewById<AppCompatImageView>(R.id.nmod_picker_item_card_view_image_view)
                fileImage.setImageResource(R.drawable.ic_folder)
                val textFileName = cardView.findViewById<AppCompatTextView>(R.id.nmod_picker_item_card_view_text_name)
                textFileName.text = currentCardViewFile.name

                cardView.setOnClickListener { select(currentCardViewFile) }
            }
            return cardView
        }

    }

    private inner class SelectHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_SELECT) {
                val file = msg.obj as File
                if (file == null) {
                    val lastFile = currentPath!!.parentFile
                    if (isValidParent)
                        openDirectory(lastFile)
                } else if (file.isDirectory)
                    openDirectory(file)
            }
        }
    }

    companion object {
        val REQUEST_PICK_DIR = 3
        val TAG_DIR_PATH = "dir_path"
        private val MSG_SELECT = 1

        fun startThisActivity(context: Activity, path: File) {
            startThisActivity(context, path.path)
        }

        @JvmOverloads
        fun startThisActivity(context: Activity, path: String = Environment.getExternalStorageDirectory().path) {
            val intent = Intent(context, DirPickerActivity::class.java)
            val extras = Bundle()
            extras.putString(TAG_DIR_PATH, path)
            intent.putExtras(extras)
            context.startActivityForResult(intent, REQUEST_PICK_DIR)
        }
    }
}
