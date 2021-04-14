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
package com.mcal.mcpelauncher.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.BaseActivity
import com.mcal.mcpelauncher.data.Constants
import com.mcal.mcpelauncher.databinding.ModdedpeDirPickerBinding
import com.mcal.mcpelauncher.databinding.NmodPickerFileItemBinding
import com.mcal.mcpelauncher.utils.ScopedStorage.storageDirectory
import java.io.File
import java.util.*

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class DirPickerActivity : BaseActivity() {
    private val mSelectHandler: SelectHandler = SelectHandler()
    private var currentPath: File? = null
    private var filesInCurrentPath: ArrayList<File>? = null
    private lateinit var binding: ModdedpeDirPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.moddedpe_dir_picker)
        binding.moddedpeDirPickerFabReset.setOnClickListener { onResetClicked() }
        binding.moddedpeDirPickerFabSelect.setOnClickListener { onSelectThisClicked() }
        setResult(RESULT_CANCELED, Intent())
        setActionBarButtonCloseRight()
        var pathString: String? = null
        try {
            pathString = intent.extras!!.getString(TAG_DIR_PATH)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        if (pathString == null) pathString = storageDirectory.absolutePath
        currentPath = File(pathString!!)
        openDirectory(currentPath!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private val isValidParent: Boolean
        get() = currentPath!!.parentFile != null && currentPath!!.parentFile!!.exists() && currentPath!!.parentFile!!.listFiles() != null && currentPath!!.parentFile!!.listFiles()!!.isNotEmpty()

    private fun onSelectThisClicked() {
        val data = Intent()
        val extras = Bundle()
        extras.putString(TAG_DIR_PATH, currentPath!!.absolutePath)
        data.putExtras(extras)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun onResetClicked() {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.dir_picker_reset_warning_title)
        dialog.setMessage(R.string.dir_picker_reset_warning_message)
        dialog.setPositiveButton(android.R.string.ok) { p1: DialogInterface, p2: Int ->
            p1.dismiss()
            val data = Intent()
            val extras = Bundle()
            extras.putString(TAG_DIR_PATH, Constants.STRING_VALUE_DEFAULT)
            data.putExtras(extras)
            setResult(RESULT_OK, data)
            finish()
        }
        dialog.setNegativeButton(android.R.string.cancel) { p1: DialogInterface, p2: Int -> p1.dismiss() }
        dialog.show()
    }

    private fun select(file_arg: File?) {
        object : Thread() {
            override fun run() {
                try {
                    sleep(450)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
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

        filesInCurrentPath!!.sortWith { o1, o2 ->
            when {
                o1.isDirectory and o2.isFile -> -1
                o1.isFile and o2.isDirectory -> 1
                else -> o1.name.compareTo(o2.name, ignoreCase = true)
            }
        }

        val fileListView = findViewById<View>(R.id.picker_dir_list_view) as ListView
        fileListView.adapter = FileAdapter()
    }

    private inner class FileAdapter : BaseAdapter() {
        override fun getCount(): Int {
            if (isValidParent) return filesInCurrentPath!!.size + 1
            return if (filesInCurrentPath!!.size == 0) 1 else filesInCurrentPath!!.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var pos = position
            @SuppressLint("ViewHolder") val itemBinding: NmodPickerFileItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.nmod_picker_file_item, parent, false)
            if (!currentPath!!.path.endsWith(File.separator)) {
                if (position == 0) {
                    itemBinding.nmodPickerItemCardViewImageView.setImageResource(R.drawable.ic_folder_outline)
                    itemBinding.nmodPickerItemCardViewTextName.text = "..."
                    itemBinding.nmodPickerItemLinearLayout.setOnClickListener { select(null) }
                } else {
                    val currentCardViewFile = filesInCurrentPath!![--pos]
                    itemBinding.nmodPickerItemCardViewImageView.setImageResource(R.drawable.ic_folder)
                    itemBinding.nmodPickerItemCardViewTextName.text = currentCardViewFile.name
                    itemBinding.nmodPickerItemLinearLayout.setOnClickListener { select(currentCardViewFile) }
                }
            } else {
                val currentCardViewFile = filesInCurrentPath!![position]
                itemBinding.nmodPickerItemCardViewImageView.setImageResource(R.drawable.ic_folder)
                itemBinding.nmodPickerItemCardViewTextName.text = currentCardViewFile.name
                itemBinding.nmodPickerItemLinearLayout.setOnClickListener { select(currentCardViewFile) }
            }
            return itemBinding.root
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class SelectHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_SELECT) {
                val file: File = msg.obj as File
                if (file.isDirectory) {
                    openDirectory(file)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_PICK_DIR = 3
        private const val TAG_DIR_PATH = "dir_path"
        private const val MSG_SELECT = 1

        fun startThisActivity(context: AppCompatActivity, path: File) {
            startThisActivity(context, path.path)
        }

        @JvmStatic
        @JvmOverloads
        fun startThisActivity(context: AppCompatActivity, path: String? = storageDirectory.path) {
            val intent = Intent(context, DirPickerActivity::class.java)
            val extras = Bundle()
            extras.putString(TAG_DIR_PATH, path)
            intent.putExtras(extras)
            context.startActivityForResult(intent, REQUEST_PICK_DIR)
        }
    }
}