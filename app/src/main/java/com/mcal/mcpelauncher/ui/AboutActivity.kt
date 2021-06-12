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
import androidx.appcompat.app.AlertDialog
import com.mcal.mcpelauncher.BuildConfig
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.activities.BaseActivity
import com.mcal.mcpelauncher.databinding.ModdedpeAboutBinding
import com.mcal.mcpelauncher.iap.DataWrappers
import com.mcal.mcpelauncher.iap.IapConnector
import com.mcal.mcpelauncher.iap.PurchaseServiceListener
import com.mcal.mcpelauncher.iap.SubscriptionServiceListener
import com.mcal.mcpelauncher.utils.I18n.setLanguage

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class AboutActivity : BaseActivity() {
    private lateinit var binding: ModdedpeAboutBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModdedpeAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBarButtonCloseRight()

        val nonConsumablesList = listOf("lifetime")
        val consumablesList = listOf("donate", "moderate", "quite", "plenty", "yearly")
        val subsList = listOf("donate")

        val iapConnector = IapConnector(
            this,
            nonConsumableKeys = nonConsumablesList,
            consumableKeys = consumablesList,
            subscriptionKeys = subsList,
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxcY9HnWLQlOrOC70CdjqiFlVvIwZ7GoK9K0po46QHOex2+WefmTVKTmSwx1hreKg0/04ODXIVt5gbwGvUAYnIEGU6SZ2jCg57pCSeg+rEcDvC73YJEUY6nlYFlERBCZv1spkbgpG+HWQ9dP0BPD+cBzZmZtbTca8lWtDnAcQKmfuTBkVH1s/UY60T30+gsfBZCtw78BYF0GnOYpesaYXWkzHH3XtHrWEsEtsyDur8SrNIMNb1iW3Q9eYzHd5kRKWwQdMeDYdPE3uG2laAhRbR9Rz8u3EzJU5lpIPLH9vHCBQpqkdJOC6knjFxFEU0hCBaqUiwB9K/oq5LJjZ9guHLQIDAQAB",
            enableLogging = BuildConfig.DEBUG
        )

        iapConnector.addPurchaseListener(object : PurchaseServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, String>) {
                // list of available products will be received here, so you can update UI with prices if needed
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                when (purchaseInfo.sku) {
                    "donate" -> {

                    }
                }
            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered fetching owned products using IapConnector;
            }
        })

        iapConnector.addSubscriptionListener(object : SubscriptionServiceListener {
            override fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered upon fetching owned subscription upon initialization
            }

            override fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered whenever subscription succeeded
                when (purchaseInfo.sku) {
                    "donate" -> {

                    }
                }
            }

            override fun onPricesUpdated(iapKeyPrices: Map<String, String>) {
                // list of available products will be received here, so you can update UI with prices if needed
            }
        })

        binding.aboutViewDonateButton.setOnClickListener {
            iapConnector.purchase(this, "donate")
        }

        binding.aboutViewGithubButton.setOnClickListener { openUri(URI_GITHUB) }
        binding.aboutViewNmodApiButton.setOnClickListener { openUri(URI_NMOD_API) }
        binding.aboutTranslatorsButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.about_translators)
                .setMessage(R.string.about_translators_message)
                .setPositiveButton(android.R.string.ok) { p11: DialogInterface, _: Int -> p11.dismiss() }
                .show()
        }
        setLanguage(this)
    }

    private fun openUri(uri: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val contentUrl = Uri.parse(uri)
        intent.data = contentUrl
        startActivity(intent)
    }

    companion object {
        private const val URI_GITHUB = "https://github.com/TimScriptov/ModdedPE.git"
        private const val URI_NMOD_API = "http://github.com/TimScriptov/NModAPI.git"
    }
}