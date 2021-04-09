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
package com.mcal.mcpelauncher.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.databinding.ModdedpeAboutBinding;
import com.mcal.mcpelauncher.databinding.XalWebviewBinding;
import com.mcal.mcpelauncher.utils.I18n;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class AboutActivity extends BaseActivity implements BillingProcessor.IBillingHandler {
    private static final String URI_GITHUB = "https://github.com/TimScriptov/ModdedPE.git";
    private static final String URI_NMOD_API = "http://github.com/TimScriptov/NModAPI.git";
    private BillingProcessor bp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModdedpeAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.moddedpe_about);

        setActionBarButtonCloseRight();

        binding.aboutViewGithubButton.setOnClickListener(p1 -> openUri(URI_GITHUB));

        binding.aboutViewNmodApiButton.setOnClickListener(p1 -> openUri(URI_NMOD_API));

        binding.aboutTranslatorsButton.setOnClickListener(p1 -> new AlertDialog.Builder(AboutActivity.this)
                .setTitle(R.string.about_translators)
                .setMessage(R.string.about_translators_message)
                .setPositiveButton(android.R.string.ok, (p11, p2) -> p11.dismiss()).show());
        bp = new BillingProcessor(this, null, this);
        I18n.setLanguage(this);
    }

    public void donate(View v) {
        bp.purchase(this, "donate");
    }

    @Override
    public void onProductPurchased(String p1, TransactionDetails p2) {
        Toast.makeText(this, "Thanks", Toast.LENGTH_LONG).show();
        bp.consumePurchase(p1);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int p1, Throwable p2) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private void openUri(String uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(uri);
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bp.handleActivityResult(requestCode, resultCode, data);
    }
}
