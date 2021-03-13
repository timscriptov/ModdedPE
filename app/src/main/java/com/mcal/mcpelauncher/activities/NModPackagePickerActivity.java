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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;
import com.mcal.pesdk.nmod.NMod;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModPackagePickerActivity extends BaseActivity {
    public static final String TAG_PACKAGE_NAME = "package_name";
    public static final int REQUEST_PICK_PACKAGE = 1;
    private static final int MSG_SHOW_LIST_VIEW = 1;
    private static final int MSG_SHOW_UNFOUND_VIEW = 2;
    private UIHandler mUIHandler = new UIHandler();
    private ArrayList<NMod> nmods = new ArrayList<>();

    public static void startThisActivity(AppCompatActivity context) {
        Intent intent = new Intent(context, NModPackagePickerActivity.class);
        context.startActivityForResult(intent, REQUEST_PICK_PACKAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nmod_picker_package);
        setResult(RESULT_CANCELED);
        setActionBarButtonCloseRight();

        View loading_view = findViewById(R.id.nmod_picker_package_loading_view);
        loading_view.setVisibility(View.VISIBLE);

        new LoadingThread().start();
    }

    private void showListView() {
        View loading_view = findViewById(R.id.nmod_picker_package_loading_view);
        loading_view.setVisibility(View.GONE);

        View list_view = findViewById(R.id.nmod_picker_package_list_view);
        list_view.setVisibility(View.VISIBLE);

        ListView list = (ListView) list_view;
        list.setAdapter(new PackageListAdapter());
    }

    private void showUnFoundView() {
        View loading_view = findViewById(R.id.nmod_picker_package_loading_view);
        loading_view.setVisibility(View.GONE);

        View view = findViewById(R.id.nmod_picker_package_unfound_view);
        view.setVisibility(View.VISIBLE);
    }

    @SuppressLint("HandlerLeak")
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SHOW_LIST_VIEW) {
                showListView();
            } else if (msg.what == MSG_SHOW_UNFOUND_VIEW) {
                showUnFoundView();
            }
        }
    }

    private class LoadingThread extends Thread {
        @Override
        public void run() {
            nmods = getPESdk().getNModAPI().findInstalledNMods();
            if (nmods.size() > 0)
                mUIHandler.sendEmptyMessage(MSG_SHOW_LIST_VIEW);
            else
                mUIHandler.sendEmptyMessage(MSG_SHOW_UNFOUND_VIEW);
        }
    }

    private class PackageListAdapter extends BaseAdapter {

        public PackageListAdapter() {
            Collections.sort(nmods, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        }

        @Override
        public int getCount() {
            return nmods.size();
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
            final NMod nmod = nmods.get(p1);
            @SuppressLint("ViewHolder") View baseCardView = getLayoutInflater().inflate(R.layout.nmod_picker_package_item, null);
            AppCompatImageView imageView = baseCardView.findViewById(R.id.nmod_picker_package_item_card_view_image_view);
            Bitmap nmodIcon = nmod.getIcon();
            if (nmodIcon == null)
                nmodIcon = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack);
            imageView.setImageBitmap(nmodIcon);
            AppCompatTextView name = baseCardView.findViewById(R.id.nmod_picker_package_item_card_view_text_name);
            name.setText(nmod.getName());
            AppCompatTextView pkgname = baseCardView.findViewById(R.id.nmod_picker_package_item_card_view_text_package_name);
            pkgname.setText(nmod.getPackageName());
            baseCardView.setOnClickListener(p11 -> {
                Intent intent = new Intent();
                Bundle extras = new Bundle();
                extras.putString(TAG_PACKAGE_NAME, nmod.getPackageName());
                intent.putExtras(extras);
                setResult(RESULT_OK, intent);
                finish();
            });
            baseCardView.setOnLongClickListener(p112 -> {
                NModDescriptionActivity.startThisActivity(NModPackagePickerActivity.this, nmod);
                return false;
            });
            return baseCardView;
        }
    }
}