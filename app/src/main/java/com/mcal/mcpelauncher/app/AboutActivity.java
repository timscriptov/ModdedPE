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
package com.mcal.mcpelauncher.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.data.Constants;
import com.mcal.mcpelauncher.utils.*;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class AboutActivity extends BaseActivity implements BillingProcessor.IBillingHandler {
    private static final String URI_GITHUB = "https://github.com/TimScriptov/ModdedPE.git";
    private static final String URI_NMOD_API = "http://github.com/TimScriptov/NModAPI.git";
    private BillingProcessor bp;

    @Override
    public void onProductPurchased(String p1, TransactionDetails p2) {
        Toast.makeText(this, "Thanks", Toast.LENGTH_LONG).show();
        bp.consumePurchase(p1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_about);

        setActionBarButtonCloseRight();

        findViewById(R.id.about_view_github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                openUri(URI_GITHUB);
            }
        });

        findViewById(R.id.about_view_nmod_api_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                openUri(URI_NMOD_API);
            }
        });

        findViewById(R.id.about_translators_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                new AlertDialog.Builder(AboutActivity.this).setTitle(R.string.about_translators).setMessage(R.string.about_translators_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        p1.dismiss();
                    }
                }).show();
            }
        });
        bp = new BillingProcessor(this, null, this);
    }

    public void donate(View v) {
        bp.purchase(this, Constants.DONATE);
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
        intent.setAction("android.intent.action.VIEW");
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
