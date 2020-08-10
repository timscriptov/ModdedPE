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
package com.mcal.mcpelauncher.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.activities.NModDescriptionActivity;
import com.mcal.pesdk.nmod.NMod;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
public class NModBanner extends RelativeLayout {
    private RelativeLayout mBannerView;
    private Random mRandom = new Random();
    private ArrayList<NMod> mNModArrayList = new ArrayList<>();

    public NModBanner(Context context) {
        super(context);
        init();
    }

    public NModBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NModBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void updateNModList() {
        ArrayList<NMod> newNModList = ModdedPEApplication.mPESdk.getNModAPI().getImportedEnabledNModsHaveBanners();
        if (mNModArrayList.isEmpty() || !mNModArrayList.equals(newNModList)) {
            mNModArrayList.clear();
            mNModArrayList.addAll(newNModList);
            if (newNModList.size() > 0)
                mBannerView = createBannerItemFor(newNModList.get(mRandom.nextInt(newNModList.size())));
            removeAllViews();
            addView(mBannerView);
        }
        invalidate();
    }

    private void init() {
        mBannerView = createEmptyBannerItem();
        addView(mBannerView);
        updateNModList();
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateNModList();
    }

    @Override
    public void addView(View child) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        super.addView(child, params);
    }

    private RelativeLayout createEmptyBannerItem() {
        return (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.moddedpe_nmod_banner_item, null);
    }

    @NotNull
    private RelativeLayout createBannerItemFor(NMod nmod_for) {
        final NMod nmod = nmod_for;
        RelativeLayout view = createEmptyBannerItem();
        AppCompatImageView image = view.findViewById(R.id.moddedpe_nmod_banner_item_image_view);
        image.setImageBitmap(nmod.getBannerImage());
        AppCompatTextView bannerTitle = view.findViewById(R.id.moddedpe_nmod_banner_item_text_view_title);
        bannerTitle.setText(nmod.getBannerTitle());
        view.setOnClickListener(p1 -> NModDescriptionActivity.startThisActivity(getContext(), nmod));
        return view;
    }
}