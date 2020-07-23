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
package com.mcal.mcpelauncher.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.mcal.mcdesign.utils.BitmapRepeater;
import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.utils.I18n;
import com.mcal.pesdk.PESdk;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class BaseActivity extends AppCompatActivity {

    protected PESdk getPESdk() {
        return ModdedPEApplication.mPESdk;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultActionBar();
        I18n.setLanguage(this);
    }

    protected void setDefaultActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            RelativeLayout actionBarCustomView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.mcd_actionbar, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(actionBarCustomView, layoutParams);
            Toolbar parent = (Toolbar) actionBarCustomView.getParent();
            parent.setContentInsetsAbsolute(0, 0);

            AppCompatTextView titleTV = actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
            titleTV.setText(getTitle());
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);

        if (getSupportActionBar() != null) {
            View actionBarCustomView = getSupportActionBar().getCustomView();
            AppCompatTextView titleTV = actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
            titleTV.setText(titleId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        if (getSupportActionBar() != null) {
            View actionBarCustomView = getSupportActionBar().getCustomView();
            AppCompatTextView titleTV = actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
            titleTV.setText(title);
        }
    }

    protected void setActionBarViewRight(View view) {
        if (getSupportActionBar() != null) {
            View actionBarCustomView = getSupportActionBar().getCustomView();
            RelativeLayout layout = actionBarCustomView.findViewById(R.id.mcd_actionbar_ViewRight);
            layout.removeAllViews();
            layout.addView(view);
        }
    }

    protected void setActionBarViewLeft(View view) {
        if (getSupportActionBar() != null) {
            View actionBarCustomView = getSupportActionBar().getCustomView();
            RelativeLayout layout = actionBarCustomView.findViewById(R.id.mcd_actionbar_ViewLeft);
            layout.removeAllViews();
            layout.addView(view);
        }
    }

    protected void setActionBarButtonCloseRight() {
        View buttonClose = getLayoutInflater().inflate(R.layout.moddedpe_ui_button_close, null);
        buttonClose.findViewById(R.id.moddedpe_ui_button_item_image_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                finish();
            }


        });
        setActionBarViewRight(buttonClose);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_bg);
        bitmap = BitmapRepeater.repeat(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), bitmap);
        getWindow().getDecorView().setBackground(new BitmapDrawable(bitmap));
    }
}
