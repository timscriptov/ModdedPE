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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;
import com.mcal.pesdk.nmod.NMod;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModDescriptionActivity extends BaseActivity {
    public final static String TAG_PACKAGE_NAME = "nmod_package_name";
    public final static String TAG_NAME = "nmod_name";
    public final static String TAG_AUTHOR = "author";
    public final static String TAG_VERSION_NAME = "version_name";
    public final static String TAG_DESCRIPTION = "description";
    public final static String TAG_ICON_PATH = "icon_path";
    public final static String TAG_CHANGE_LOG = "change_log";
    public final static String TAG_MINECRAFT_VERSION_NAME = "minecraft_version_name";

    public static void startThisActivity(Context context, @NotNull NMod nmod) {
        Intent intent = new Intent(context, NModDescriptionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TAG_PACKAGE_NAME, nmod.getPackageName());
        bundle.putString(TAG_NAME, nmod.getName());
        bundle.putString(TAG_DESCRIPTION, nmod.getDescription());
        bundle.putString(TAG_AUTHOR, nmod.getAuthor());
        bundle.putString(TAG_VERSION_NAME, nmod.getVersionName());
        bundle.putString(TAG_CHANGE_LOG, nmod.getChangeLog());
        bundle.putString(TAG_MINECRAFT_VERSION_NAME, nmod.getMinecraftVersionName());
        File iconPath = nmod.copyIconToData();
        if (iconPath != null)
            bundle.putString(TAG_ICON_PATH, iconPath.getAbsolutePath());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_nmod_description);

        String nmodPackageName = getIntent().getExtras().getString(TAG_PACKAGE_NAME);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack);
        try {
            String iconpath = getIntent().getExtras().getString(TAG_ICON_PATH);
            FileInputStream fileInput = new FileInputStream(iconpath);
            icon = BitmapFactory.decodeStream(fileInput);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        String description = getIntent().getExtras().getString(TAG_DESCRIPTION);
        String name = getIntent().getExtras().getString(TAG_NAME);
        String version_name = getIntent().getExtras().getString(TAG_VERSION_NAME);
        String author = getIntent().getExtras().getString(TAG_AUTHOR);
        String change_log = getIntent().getExtras().getString(TAG_CHANGE_LOG);
        String minecraft_version_name = getIntent().getExtras().getString(TAG_MINECRAFT_VERSION_NAME);

        setTitle(name);
        setActionBarButtonCloseRight();

        AppCompatImageView iconImage = (AppCompatImageView) findViewById(R.id.moddedpenmoddescriptionImageViewIcon);
        iconImage.setImageBitmap(icon);

        AppCompatTextView textViewName = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewNModName);
        textViewName.setText(name);
        AppCompatTextView textViewPackageName = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewNModPackageName);
        textViewPackageName.setText(nmodPackageName);
        AppCompatTextView textViewDescription = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewDescription);
        textViewDescription.setText(description == null ? getString(R.string.nmod_description_unknow) : description);
        AppCompatTextView textViewAuthor = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewAuthor);
        textViewAuthor.setText(author == null ? getString(R.string.nmod_description_unknow) : author);
        AppCompatTextView textViewVersionName = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewVersionName);
        textViewVersionName.setText(version_name == null ? getString(R.string.nmod_description_unknow) : version_name);
        AppCompatTextView textViewWhatsNew = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewWhatsNew);
        textViewWhatsNew.setText(change_log == null ? getString(R.string.nmod_description_unknow) : change_log);
        AppCompatTextView textViewMinecraftVersionName = (AppCompatTextView) findViewById(R.id.moddedpenmoddescriptionTextViewMinecraftVersionName);
        textViewMinecraftVersionName.setText(minecraft_version_name == null ? getString(R.string.nmod_description_unknow) : minecraft_version_name);
    }
}