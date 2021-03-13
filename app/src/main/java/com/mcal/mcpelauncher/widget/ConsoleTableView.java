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
package com.mcal.mcpelauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.BuildConfig;
import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.mcpelauncher.R;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
public class ConsoleTableView extends RelativeLayout {
    public ConsoleTableView(Context c) {
        super(c);
        addTableView();
    }

    public ConsoleTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTableView();
    }

    public ConsoleTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTableView();
    }

    public ConsoleTableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addTableView();
    }

    @SuppressLint("SetTextI18n")
    private void addTableView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.moddedpe_main_console_table, null);

        ((AppCompatTextView) rootView.findViewById(R.id.moddedpe_main_text_view_app_version)).setText(getContext().getResources().getString(R.string.copyright));
        ((AppCompatTextView) rootView.findViewById(R.id.moddedpe_main_text_view_target_mc_version)).setTextColor(ModdedPEApplication.mPESdk.getMinecraftInfo().isSupportedMinecraftVersion(getContext().getResources().getStringArray(R.array.target_mcpe_versions)) ? Color.GREEN : Color.RED);
        ((AppCompatTextView) rootView.findViewById(R.id.moddedpe_main_text_view_target_mc_version)).setText(BuildConfig.VERSION_NAME);
        addView(rootView);
    }
}