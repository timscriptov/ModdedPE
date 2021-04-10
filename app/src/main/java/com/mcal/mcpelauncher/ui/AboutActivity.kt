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

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.TransactionDetails
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.BaseActivity
import com.mcal.mcpelauncher.databinding.ModdedpeAboutBinding
import com.mcal.mcpelauncher.utils.I18n.setLanguage

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class AboutActivity : BaseActivity(), IBillingHandler {
    private var bp: BillingProcessor? = null
    private lateinit var binding: ModdedpeAboutBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModdedpeAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBarButtonCloseRight()
        binding.aboutViewGithubButton.setOnClickListener { p1: View? -> openUri(URI_GITHUB) }
        binding.aboutViewNmodApiButton.setOnClickListener { p1: View? -> openUri(URI_NMOD_API) }
        binding.aboutTranslatorsButton.setOnClickListener { p1: View? ->
            AlertDialog.Builder(this@AboutActivity)
                    .setTitle(R.string.about_translators)
                    .setMessage(R.string.about_translators_message)
                    .setPositiveButton(android.R.string.ok) { p11: DialogInterface, p2: Int -> p11.dismiss() }.show()
        }
        bp = BillingProcessor(this, null, this)
        setLanguage(this)
    }

    fun donate(v: View?) {
        bp!!.purchase(this, "donate")
    }

    override fun onProductPurchased(p1: String, p2: TransactionDetails?) {
        Toast.makeText(this, "Thanks", Toast.LENGTH_LONG).show()
        bp!!.consumePurchase(p1)
    }

    override fun onPurchaseHistoryRestored() {}

    override fun onBillingError(p1: Int, p2: Throwable?) {}

    override fun onBillingInitialized() {}

    private fun openUri(uri: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val contentUrl = Uri.parse(uri)
        intent.data = contentUrl
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bp!!.handleActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val URI_GITHUB = "https://github.com/TimScriptov/ModdedPE.git"
        private const val URI_NMOD_API = "http://github.com/TimScriptov/NModAPI.git"
    }
}