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
package com.mcal.mcpelauncher.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.app.NModDescriptionActivity
import com.mcal.pesdk.nmod.NMod
import java.util.*

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class NModBanner : RelativeLayout {
    private var mBannerView: RelativeLayout? = null
    private val mRandom = Random()
    private val mNModArrayList = ArrayList<NMod>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun updateNModList() {
        val newNModList = ModdedPEApplication.mPESdk.nModAPI.importedEnabledNModsHaveBanners
        if (mNModArrayList.isEmpty() || mNModArrayList != newNModList) {
            mNModArrayList.clear()
            mNModArrayList.addAll(newNModList)
            if (newNModList.size > 0)
                mBannerView = createBannerItemFor(newNModList[mRandom.nextInt(newNModList.size)])
            removeAllViews()
            addView(mBannerView)
        }
        invalidate()
    }

    private fun init() {
        mBannerView = createEmptyBannerItem()
        addView(mBannerView)
        updateNModList()
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateNModList()
    }

    override fun addView(child: View?) {
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        super.addView(child, params)
    }

    private fun createEmptyBannerItem(): RelativeLayout {
        return LayoutInflater.from(context).inflate(R.layout.moddedpe_nmod_banner_item, null) as RelativeLayout
    }

    private fun createBannerItemFor(nmod_for: NMod): RelativeLayout {
        val view = createEmptyBannerItem()
        val image = view.findViewById<AppCompatImageView>(R.id.moddedpe_nmod_banner_item_image_view)
        image.setImageBitmap(nmod_for.bannerImage)
        val bannerTitle = view.findViewById<AppCompatTextView>(R.id.moddedpe_nmod_banner_item_text_view_title)
        bannerTitle.text = nmod_for.bannerTitle
        view.setOnClickListener { NModDescriptionActivity.startThisActivity(context, nmod_for) }
        return view
    }
}
