/*
 * Copyright (C) 2018-2020 Тимашков Иван
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.mcal.mcpelauncher.R;
import com.mcal.pesdk.nmod.NMod;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModLoadFailActivity extends BaseActivity {
    private static final String KEY_TYPE_STRING = "type_string";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_ICON_PATH = "icon_path";
    private static final String KEY_MC_DATA = "mc_data";

    private ArrayList<String> mPackageNames = new ArrayList<>();
    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mTypeStrings = new ArrayList<>();
    private ArrayList<String> mIconPaths = new ArrayList<>();
    private Bundle mMCData;

    public static void startThisActivity(Context context, @NotNull ArrayList<NMod> nmods, Bundle data) {
        Intent intent = new Intent(context, NModLoadFailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        ArrayList<String> mPackageNames = new ArrayList<>();
        ArrayList<String> mMessages = new ArrayList<>();
        ArrayList<String> mTypeStrings = new ArrayList<>();
        ArrayList<String> mIconPaths = new ArrayList<>();
        for (NMod nmod : nmods) {
            mPackageNames.add(nmod.getPackageName());
            mMessages.add(nmod.getLoadException().getCause().toString());
            mTypeStrings.add(nmod.getLoadException().toTypeString());
            File iconPath = nmod.copyIconToData();
            if (iconPath != null)
                mIconPaths.add(iconPath.getAbsolutePath());
            else
                mIconPaths.add(null);
        }
        bundle.putStringArrayList(KEY_MESSAGE, mMessages);
        bundle.putStringArrayList(KEY_ICON_PATH, mIconPaths);
        bundle.putStringArrayList(KEY_TYPE_STRING, mTypeStrings);
        bundle.putStringArrayList(KEY_PACKAGE_NAME, mPackageNames);
        bundle.putBundle(KEY_MC_DATA, data);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_nmod_load_failed);

        mMessages = getIntent().getExtras().getStringArrayList(KEY_MESSAGE);
        mIconPaths = getIntent().getExtras().getStringArrayList(KEY_ICON_PATH);
        mTypeStrings = getIntent().getExtras().getStringArrayList(KEY_TYPE_STRING);
        mPackageNames = getIntent().getExtras().getStringArrayList(KEY_PACKAGE_NAME);
        mMCData = getIntent().getExtras().getBundle(KEY_MC_DATA);

        ListView errorListView = findViewById(R.id.nmod_load_failed_list_view);
        errorListView.setAdapter(new ViewAdapter());

        findViewById(R.id.load_failed_next_button).setOnClickListener(p1 -> onNextClicked());
    }

    private void onNextClicked() {
        Intent intent = new Intent(this, MinecraftActivity.class);
        intent.putExtras(mMCData);
        startActivity(intent);
        finish();
    }

    private class ViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mPackageNames.size();
        }

        @Override
        public Object getItem(int p1) {
            return p1;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }

        @Override
        public View getView(int p1, View p2, ViewGroup p3) {
            @SuppressLint("ViewHolder") CardView view = (CardView) getLayoutInflater().inflate(R.layout.moddedpe_nmod_load_failed_item_card, null);
            AppCompatTextView packageNameTextView = view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_package_name);
            packageNameTextView.setText(mPackageNames.get(p1));
            AppCompatTextView errorMessageTextView = view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_message);
            errorMessageTextView.setText(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(p1), mMessages.get(p1)}));
            AppCompatImageView imageViewIcon = view.findViewById(R.id.moddedpe_nmod_load_failed_item_card_icon);
            try {
                if (mIconPaths.get(p1) != null)
                    imageViewIcon.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(mIconPaths.get(p1))));
                else
                    imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack));
            } catch (FileNotFoundException e) {
                imageViewIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack));
            }
            final int index = p1;
            view.setOnClickListener(p112 -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(NModLoadFailActivity.this, R.style.AlertDialogTheme);
                dialog.setTitle(R.string.load_fail_title);
                dialog.setMessage(getString(R.string.load_fail_msg, new Object[]{mTypeStrings.get(index), mMessages.get(index)}));
                dialog.setPositiveButton(android.R.string.ok, (p11, p21) -> p11.dismiss());
                dialog.show();
            });
            return view;
        }
    }
}