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
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.data.Constants

class AboutActivity : BaseActivity(), BillingProcessor.IBillingHandler {
    private var bp: BillingProcessor? = null

    override fun onProductPurchased(p1: String, p2: TransactionDetails?) {
        Toast.makeText(this, "Thanks", Toast.LENGTH_LONG).show()
        bp!!.consumePurchase(p1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_about)

        setActionBarButtonCloseRight()

        findViewById<View>(R.id.about_view_github_button).setOnClickListener { openUri(URI_GITHUB) }

        findViewById<View>(R.id.about_view_nmod_api_button).setOnClickListener { openUri(URI_NMOD_API) }

        findViewById<View>(R.id.about_translators_button).setOnClickListener { AlertDialog.Builder(this@AboutActivity).setTitle(R.string.about_translators).setMessage(R.string.about_translators_message).setPositiveButton(android.R.string.ok) { p1, p2 -> p1.dismiss() }.show() }
        bp = BillingProcessor(this, null, this)
    }

    fun donate(v: View) {
        bp!!.purchase(this, Constants.DONATE)
    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onBillingError(p1: Int, p2: Throwable?) {

    }

    override fun onBillingInitialized() {

    }

    private fun openUri(uri: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(uri)
        intent.data = content_url
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bp!!.handleActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val URI_GITHUB = "https://github.com/TimScriptov/ModdedPE.git"
        private val URI_NMOD_API = "http://github.com/TimScriptov/NModAPI.git"
    }
}
