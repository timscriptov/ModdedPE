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
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.databinding.DataBindingUtil
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.BaseActivity
import com.mcal.mcpelauncher.activities.NModDescriptionActivity
import com.mcal.mcpelauncher.databinding.NmodImporterFailedBinding
import com.mcal.mcpelauncher.databinding.NmodImporterFailedMsgBinding
import com.mcal.mcpelauncher.databinding.NmodImporterLoadingBinding
import com.mcal.mcpelauncher.databinding.NmodImporterSucceedBinding
import com.mcal.pesdk.nmod.ExtractFailedException
import com.mcal.pesdk.nmod.NMod
import java.io.File
import java.net.URI
import java.net.URISyntaxException

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class ImportNModActivity : BaseActivity() {
    private lateinit var binding: NmodImporterLoadingBinding

    private val mUIHandler: UIHandler = UIHandler()
    private var mTargetNMod: NMod? = null
    private var mFailedInfo: ExtractFailedException? = null
    private var activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.nmod_importer_loading)
        activity = this
        setActionBarButtonCloseRight()
        setTitle(R.string.import_nmod_title)
        val targetFile = targetNModFile
        ImportThread(targetFile).start()
    }

    private val targetNModFile: File?
        get() {
            try {
                val intent = intent
                val uri = intent.data
                return File(URI(uri.toString()))
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return null
        }

    private fun onViewMoreClicked() {
        NModDescriptionActivity.startThisActivity(this, mTargetNMod!!)
    }

    private fun onFailedViewMoreClicked() {
        val binding: NmodImporterFailedBinding = DataBindingUtil.setContentView(this, R.layout.nmod_importer_failed)
        binding.nmodImporterFailedTextView.text = getString(R.string.nmod_import_failed_full_info_message, mFailedInfo!!.toTypeString(), mFailedInfo!!.cause.toString())
    }

    private inner class ImportThread(private val mTargetFile: File?) : Thread() {
        override fun run() {
            super.run()
            try {
                val zippedNMod = peSdk.nModAPI.archiveZippedNMod(mTargetFile!!.absolutePath)
                peSdk.nModAPI.importNMod(zippedNMod)
                val msg = Message()
                msg.what = MSG_SUCCEED
                msg.obj = zippedNMod
                mUIHandler.sendMessage(msg)
            } catch (archiveFailedException: ExtractFailedException) {
                val msg = Message()
                msg.what = MSG_FAILED
                msg.obj = archiveFailedException
                mUIHandler.sendMessage(msg)
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class UIHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_SUCCEED) {
                val binding: NmodImporterSucceedBinding = DataBindingUtil.setContentView(activity!!, R.layout.nmod_importer_succeed)
                binding.importSucceedViewMoreButton.setOnClickListener { p1: View? -> onViewMoreClicked() }
                mTargetNMod = msg.obj as NMod
            } else if (msg.what == MSG_FAILED) {
                val binding: NmodImporterFailedMsgBinding = DataBindingUtil.setContentView(activity!!, R.layout.nmod_importer_failed_msg)
                binding.importFailedViewMoreButton.setOnClickListener { p1: View? -> onFailedViewMoreClicked() }
                mFailedInfo = msg.obj as ExtractFailedException
                setTitle(R.string.nmod_import_failed)
                when (mFailedInfo!!.type) {
                    ExtractFailedException.TYPE_DECODE_FAILED -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_decode)
                    ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_inequal_package_name)
                    ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_invalid_package_name)
                    ExtractFailedException.TYPE_IO_EXCEPTION -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_io_exception)
                    ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_manifest_json_syntax_error)
                    ExtractFailedException.TYPE_NO_MANIFEST -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_no_manifest)
                    ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_no_package_name)
                    ExtractFailedException.TYPE_REDUNDANT_MANIFEST -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_no_package_name)
                    else -> binding.nmodImportFailedTitleTextView.setText(R.string.nmod_import_failed_message_unexpected)
                }
            }
        }
    }

    companion object {
        private const val MSG_SUCCEED = 1
        private const val MSG_FAILED = 2
    }
}