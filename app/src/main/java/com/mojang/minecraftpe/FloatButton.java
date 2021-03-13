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
package com.mojang.minecraftpe;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.mcal.mcpelauncher.R;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FloatButton extends PopupWindow {
    private final Context mContext;

    public FloatButton(Context ctx) {
        mContext = ctx;
        init();
    }

    public void showHoverMenu() {
        ListView optionsListView = new ListView(mContext);
        String[] values = {"AlertDialog", "Toast"};
        optionsListView.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, values));

        optionsListView.setOnItemClickListener((parent, view, position, id) -> {
            TextView textView = (TextView) view;
            String text = textView.getText().toString();

            if (text.equals("AlertDialog")) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
                mDialog.setTitle("About");
                mDialog.setMessage("ModdedPE - open source Minecraft launcher");
                mDialog.show();
            } else if (text.equals("Toast")) {
                Toast.makeText(mContext, "ModdedPE - open source Minecraft launcher", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
        mDialog.setTitle("About");
        mDialog.setView(optionsListView);
        mDialog.show();
    }

    public void init() {
        // Create layout and the button.
        LinearLayout layout = new LinearLayout(mContext);
        AppCompatButton button = new AppCompatButton(mContext);
        button.setBackgroundResource(R.mipmap.ic_launcher_round);
        button.setOnClickListener(v -> showHoverMenu());
        layout.addView(button);
        setContentView(layout);

        // Set dimensions.
        setWidth(128);
        setHeight(128);
    }
}