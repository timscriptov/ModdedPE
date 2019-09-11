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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.utils.DataPreloader
import com.mcal.pesdk.nmod.ExtractFailedException
import com.mcal.pesdk.nmod.NMod
import java.util.*

class MainManageNModFragment : BaseFragment(), DataPreloader.PreloadingFinishedListener {
    private var mListView: ListView? = null
    private var mRootView: View? = null
    private val mNModProcesserHandler = NModProcesserHandler()
    private var mProcessingDialog: AlertDialog? = null
    private val mReloadHandler = ReloadHandler()
    private var mReloadDialog: AlertDialog? = null
    private var mDataPreloader: DataPreloader? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.moddedpe_manage_nmod, null)

        mListView = mRootView!!.findViewById(R.id.moddedpe_manage_nmod_list_view)

        refreshNModDatas()

        val addBtn = mRootView!!.findViewById<FloatingActionButton>(R.id.moddedpe_manage_nmod_add_new)
        addBtn.setOnClickListener { onAddNewNMod() }
        return mRootView
    }

    override fun onStart() {
        super.onStart()
        if (mDataPreloader == null && !peSdk.isInited) {
            mReloadDialog = AlertDialog.Builder(activity!!).setTitle(R.string.main_reloading_title).setView(R.layout.moddedpe_main_reload_dialog).setCancelable(false).create()
            mReloadDialog!!.show()
            mDataPreloader = DataPreloader(this)
            mDataPreloader!!.preload(activity!!.applicationContext)
        }
    }

    override fun onPreloadingFinished() {
        mReloadHandler.sendEmptyMessage(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NModPackagePickerActivity.REQUEST_PICK_PACKAGE) {
                //picked from package
                onPickedNModFromPackage(data!!.extras!!.getString(NModPackagePickerActivity.TAG_PACKAGE_NAME))
            } else if (requestCode == NModFilePickerActivity.REQUEST_PICK_FILE) {
                //picked from storage
                onPickedNModFromStorage(data!!.extras!!.getString(NModFilePickerActivity.TAG_FILE_PATH))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun onPickedNModFromStorage(path: String?) {
        object : Thread() {
            override fun run() {
                mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG)
                try {
                    val zippedNMod = peSdk.nModAPI.archiveZippedNMod(path!!)
                    if (peSdk.nModAPI.importNMod(zippedNMod)) {
                        //replaced
                        mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                        mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_REPLACED_DIALOG)
                    } else {
                        mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                        mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_SUCCEED_DIALOG)
                    }
                    mNModProcesserHandler.sendEmptyMessage(MSG_REFRESH_NMOD_DATA)

                } catch (archiveFailedException: ExtractFailedException) {
                    mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                    val message = Message()
                    message.what = MSG_SHOW_FAILED_DIALOG
                    message.obj = archiveFailedException
                    mNModProcesserHandler.sendMessage(message)
                }

            }
        }.start()
    }

    fun onPickedNModFromPackage(packageName: String?) {
        object : Thread() {
            override fun run() {
                mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG)
                try {
                    val packagedNMod = peSdk.nModAPI.archivePackagedNMod(packageName!!)
                    if (peSdk.nModAPI.importNMod(packagedNMod)) {
                        //replaced
                        mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                        mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_REPLACED_DIALOG)
                    } else {
                        mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                        mNModProcesserHandler.sendEmptyMessage(MSG_SHOW_SUCCEED_DIALOG)
                    }
                    mNModProcesserHandler.sendEmptyMessage(MSG_REFRESH_NMOD_DATA)

                } catch (archiveFailedException: ExtractFailedException) {
                    mNModProcesserHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DIALOG)
                    val message = Message()
                    message.what = MSG_SHOW_FAILED_DIALOG
                    message.obj = archiveFailedException
                    mNModProcesserHandler.sendMessage(message)
                }

            }
        }.start()
    }

    fun showPickNModFailedDialog(archiveFailedException: ExtractFailedException) {
        val alertBuilder = AlertDialog.Builder(activity!!).setTitle(R.string.nmod_import_failed).setPositiveButton(android.R.string.ok) { p1, p2 -> p1.dismiss() }
        when (archiveFailedException.type) {
            ExtractFailedException.TYPE_DECODE_FAILED -> alertBuilder.setMessage(R.string.nmod_import_failed_message_decode)
            ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME -> alertBuilder.setMessage(R.string.nmod_import_failed_message_inequal_package_name)
            ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME -> alertBuilder.setMessage(R.string.nmod_import_failed_message_invalid_package_name)
            ExtractFailedException.TYPE_IO_EXCEPTION -> alertBuilder.setMessage(R.string.nmod_import_failed_message_io_exception)
            ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION -> alertBuilder.setMessage(R.string.nmod_import_failed_message_manifest_json_syntax_error)
            ExtractFailedException.TYPE_NO_MANIFEST -> alertBuilder.setMessage(R.string.nmod_import_failed_message_no_manifest)
            ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME -> alertBuilder.setMessage(R.string.nmod_import_failed_message_no_package_name)
            ExtractFailedException.TYPE_REDUNDANT_MANIFEST -> alertBuilder.setMessage(R.string.nmod_import_failed_message_no_package_name)
            else -> alertBuilder.setMessage(R.string.nmod_import_failed_message_unexpected)
        }
        if (archiveFailedException.cause != null) {
            alertBuilder.setNegativeButton(R.string.nmod_import_failed_button_full_info) { p1, p2 ->
                p1.dismiss()
                AlertDialog.Builder(activity!!).setTitle(R.string.nmod_import_failed_full_info_title).setMessage(activity!!.resources.getString(R.string.nmod_import_failed_full_info_message, archiveFailedException.toTypeString(), archiveFailedException.cause.toString())).setPositiveButton(android.R.string.ok) { p1_, p2 -> p1_.dismiss() }.show()
            }
        }
        alertBuilder.show()
    }

    fun refreshNModDatas() {
        if (peSdk.nModAPI.importedEnabledNMods.isEmpty() && peSdk.nModAPI.importedDisabledNMods.isEmpty()) {
            mRootView!!.findViewById<View>(R.id.moddedpe_manage_nmod_layout_nmods).visibility = View.GONE
            mRootView!!.findViewById<View>(R.id.moddedpe_manage_nmod_layout_no_found).visibility = View.VISIBLE
        } else {
            mRootView!!.findViewById<View>(R.id.moddedpe_manage_nmod_layout_nmods).visibility = View.VISIBLE
            mRootView!!.findViewById<View>(R.id.moddedpe_manage_nmod_layout_no_found).visibility = View.GONE
        }

        val adapterList = NModListAdapter()
        mListView!!.adapter = adapterList
    }

    private fun showBugDialog(nmod: NMod) {
        if (!nmod.isBugPack)
            return
        AlertDialog.Builder(activity!!)
                .setTitle(R.string.load_fail_title)
                .setMessage(getString(R.string.load_fail_msg, nmod.loadException!!.toTypeString(),
                        nmod.loadException!!.cause.toString())).setPositiveButton(android.R.string.ok) { p1, _ ->
                    p1.dismiss()
                }
                .show()
    }

    private fun createCutlineView(textResId: Int): View {
        val convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_ui_cutline, null)
        val textTitle = convertView.findViewById<AppCompatTextView>(R.id.moddedpe_cutline_textview)
        textTitle.setText(textResId)
        return convertView
    }

    private fun createAddNewView(): View {
        val convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_nmod_item_new, null)
        convertView.setOnClickListener { onAddNewNMod() }
        return convertView
    }

    private fun createDisabledNModView(nmod_: NMod): View {
        var convertView: View? = null
        if (nmod_.isBugPack) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_nmod_item_bugged, null)
            val textTitle = convertView!!.findViewById<AppCompatTextView>(R.id.nmod_bugged_item_card_view_text_name)
            textTitle.text = nmod_.name
            val textPkgTitle = convertView.findViewById<AppCompatTextView>(R.id.nmod_bugged_item_card_view_text_package_name)
            textPkgTitle.text = nmod_.packageName
            val imageIcon = convertView.findViewById<AppCompatImageView>(R.id.nmod_bugged_item_card_view_image_view)
            var nmodIcon: Bitmap? = nmod_.icon
            if (nmodIcon == null)
                nmodIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
            imageIcon.setImageBitmap(nmodIcon)
            val infoButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_bugged_info)
            val onInfoClickedListener = View.OnClickListener { showBugDialog(nmod_) }
            val deleteButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_bugged_delete)
            deleteButton.setOnClickListener {
                AlertDialog.Builder(activity!!).setTitle(R.string.nmod_delete_title).setMessage(R.string.nmod_delete_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
                    peSdk.nModAPI.removeImportedNMod(nmod_)
                    refreshNModDatas()
                    p1.dismiss()
                }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
            }
            infoButton.setOnClickListener(onInfoClickedListener)
            convertView.setOnClickListener(onInfoClickedListener)
            return convertView
        }
        convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_nmod_item_disabled, null)
        val textTitle = convertView!!.findViewById<AppCompatTextView>(R.id.nmod_disabled_item_card_view_text_name)
        textTitle.text = nmod_.name
        val textPkgTitle = convertView.findViewById<AppCompatTextView>(R.id.nmod_disabled_item_card_view_text_package_name)
        textPkgTitle.text = nmod_.packageName
        val imageIcon = convertView.findViewById<AppCompatImageView>(R.id.nmod_disabled_item_card_view_image_view)
        var nmodIcon: Bitmap? = nmod_.icon
        if (nmodIcon == null)
            nmodIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
        imageIcon.setImageBitmap(nmodIcon)
        val addButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_disabled_add)
        addButton.setOnClickListener {
            peSdk.nModAPI.setEnabled(nmod_, true)
            refreshNModDatas()
        }
        val deleteButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_disabled_delete)
        deleteButton.setOnClickListener {
            AlertDialog.Builder(activity!!).setTitle(R.string.nmod_delete_title).setMessage(R.string.nmod_delete_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
                peSdk.nModAPI.removeImportedNMod(nmod_)
                refreshNModDatas()
                p1.dismiss()
            }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
        }
        convertView.setOnClickListener { NModDescriptionActivity.startThisActivity(activity!!, nmod_) }
        return convertView
    }

    private fun createEnabledNModView(nmod_: NMod): View {
        var convertView: View? = null
        if (nmod_.isBugPack) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_nmod_item_bugged, null)
            val textTitle = convertView!!.findViewById<AppCompatTextView>(R.id.nmod_bugged_item_card_view_text_name)
            textTitle.text = nmod_.name
            val textPkgTitle = convertView.findViewById<AppCompatTextView>(R.id.nmod_bugged_item_card_view_text_package_name)
            textPkgTitle.text = nmod_.packageName
            val imageIcon = convertView.findViewById<AppCompatImageView>(R.id.nmod_bugged_item_card_view_image_view)
            var nmodIcon: Bitmap? = nmod_.icon
            if (nmodIcon == null)
                nmodIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
            imageIcon.setImageBitmap(nmodIcon)
            val infoButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_bugged_info)
            val onInfoClickedListener = View.OnClickListener { showBugDialog(nmod_) }
            val deleteButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_bugged_delete)
            deleteButton.setOnClickListener {
                AlertDialog.Builder(activity!!).setTitle(R.string.nmod_delete_title).setMessage(R.string.nmod_delete_message).setPositiveButton(android.R.string.ok) { p1, p2 ->
                    peSdk.nModAPI.removeImportedNMod(nmod_)
                    refreshNModDatas()
                    p1.dismiss()
                }.setNegativeButton(android.R.string.cancel) { p1, p2 -> p1.dismiss() }.show()
            }
            infoButton.setOnClickListener(onInfoClickedListener)
            convertView.setOnClickListener(onInfoClickedListener)
            return convertView
        }
        convertView = LayoutInflater.from(activity).inflate(R.layout.moddedpe_nmod_item_active, null)
        val textTitle = convertView!!.findViewById<AppCompatTextView>(R.id.nmod_enabled_item_card_view_text_name)
        textTitle.text = nmod_.name
        val textPkgTitle = convertView.findViewById<AppCompatTextView>(R.id.nmod_enabled_item_card_view_text_package_name)
        textPkgTitle.text = nmod_.packageName
        val imageIcon = convertView.findViewById<AppCompatImageView>(R.id.nmod_enabled_item_card_view_image_view)
        var nmodIcon: Bitmap? = nmod_.icon
        if (nmodIcon == null)
            nmodIcon = BitmapFactory.decodeResource(resources, R.drawable.mcd_null_pack)
        imageIcon.setImageBitmap(nmodIcon)
        val minusButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_enabled_minus)
        minusButton.setOnClickListener {
            peSdk.nModAPI.setEnabled(nmod_, false)
            refreshNModDatas()
        }
        val downButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_enabled_arrow_down)
        downButton.setOnClickListener {
            peSdk.nModAPI.downPosNMod(nmod_)
            refreshNModDatas()
        }
        val upButton = convertView.findViewById<AppCompatImageButton>(R.id.nmod_enabled_arrow_up)
        upButton.setOnClickListener {
            peSdk.nModAPI.upPosNMod(nmod_)
            refreshNModDatas()
        }
        convertView.setOnClickListener { NModDescriptionActivity.startThisActivity(activity!!, nmod_) }
        return convertView
    }

    private fun onAddNewNMod() {
        AlertDialog.Builder(activity!!).setTitle(R.string.nmod_add_new_title).setMessage(R.string.nmod_add_new_message).setNegativeButton(R.string.nmod_add_new_pick_installed) { p1, p2 ->
            NModPackagePickerActivity.startThisActivity(activity!!)
            p1.dismiss()
        }.setPositiveButton(R.string.nmod_add_new_pick_storage) { p1, p2 ->
            if (checkPermissions())
                NModFilePickerActivity.startThisActivity(activity!!)
            p1.dismiss()
        }.show()
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
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
                NModFilePickerActivity.startThisActivity(activity!!)
            } else {
                showPermissionDinedDialog()
            }
        }
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

    private inner class ReloadHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (mReloadDialog != null) {
                refreshNModDatas()
                mReloadDialog!!.dismiss()
                mReloadDialog = null
            }
        }
    }

    private inner class NModProcesserHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_SHOW_PROGRESS_DIALOG -> mProcessingDialog = AlertDialog.Builder(activity!!).setTitle(R.string.nmod_importing_title).setView(R.layout.moddedpe_manage_nmod_progress_dialog_view).setCancelable(false).show()
                MSG_HIDE_PROGRESS_DIALOG -> {
                    if (mProcessingDialog != null)
                        mProcessingDialog!!.hide()
                    mProcessingDialog = null
                }
                MSG_SHOW_SUCCEED_DIALOG ->

                    AlertDialog.Builder(activity!!).setTitle(R.string.nmod_import_succeed_title).setMessage(R.string.nmod_import_succeed_message).setPositiveButton(android.R.string.ok) { p1, p2 -> p1.dismiss() }.show()
                MSG_SHOW_REPLACED_DIALOG -> AlertDialog.Builder(activity!!).setTitle(R.string.nmod_import_replaced_title).setMessage(R.string.nmod_import_replaced_message).setPositiveButton(android.R.string.ok) { p1, p2 -> p1.dismiss() }.show()
                MSG_SHOW_FAILED_DIALOG -> showPickNModFailedDialog(msg.obj as ExtractFailedException)
                MSG_REFRESH_NMOD_DATA -> refreshNModDatas()
            }
        }
    }

    private inner class NModListAdapter internal constructor() : BaseAdapter() {
        private val mImportedEnabledNMods = ArrayList<NMod>()
        private val mImportedDisabledNMods = ArrayList<NMod>()

        init {
            mImportedEnabledNMods.addAll(peSdk.nModAPI.importedEnabledNMods)
            mImportedDisabledNMods.addAll(peSdk.nModAPI.importedDisabledNMods)
        }

        override fun getCount(): Int {
            var count = mImportedEnabledNMods.size + mImportedDisabledNMods.size + 2
            if (mImportedEnabledNMods.size > 0)
                ++count
            return count
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val shouldShowEnabledList = mImportedEnabledNMods.size > 0 && position < mImportedEnabledNMods.size + 1
            if (shouldShowEnabledList) {
                if (position == 0) {
                    return createCutlineView(R.string.nmod_enabled_title)
                } else {
                    val nmodIndex = position - 1
                    return createEnabledNModView(mImportedEnabledNMods[nmodIndex])
                }
            }
            val disableStartPosition = if (mImportedEnabledNMods.size > 0) mImportedEnabledNMods.size + 1 else 0
            if (position == disableStartPosition) {
                return createCutlineView(R.string.nmod_disabled_title)
            }
            val itemInListPosition = position - 1 - disableStartPosition
            return if (itemInListPosition >= 0 && itemInListPosition < mImportedDisabledNMods.size) {
                createDisabledNModView(mImportedDisabledNMods[itemInListPosition])
            } else createAddNewView()
        }

    }

    companion object {
        private val MSG_SHOW_PROGRESS_DIALOG = 1
        private val MSG_HIDE_PROGRESS_DIALOG = 2
        private val MSG_SHOW_SUCCEED_DIALOG = 3
        private val MSG_SHOW_REPLACED_DIALOG = 4
        private val MSG_SHOW_FAILED_DIALOG = 5
        private val MSG_REFRESH_NMOD_DATA = 6
    }
}
