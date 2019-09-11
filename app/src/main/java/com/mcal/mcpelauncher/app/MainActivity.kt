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
package com.mcal.mcpelauncher.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.utils.UtilsSettings
import java.util.*

class MainActivity : BaseActivity(), BackgroundSoundPlayer {
    private lateinit var sc: ServiceConnection
    private var ss: SoundService? = null
    private var bound: Boolean = false
    private var paused: Boolean = false
    private var mMainViewPager: ViewPager? = null
    private var mManageNModFragment: MainManageNModFragment? = null
    private var mMainSettingsFragment: MainSettingsFragment? = null

    override fun bind() {
        bindService(Intent(this, SoundService::class.java), sc, Context.BIND_AUTO_CREATE)
    }

    override fun unbind() {
        unbindService(sc)
    }

    override fun play() {
        if (bound && paused) {
            ss!!.play()
            paused = false
        }
    }

    override fun pause() {
        if (bound && !paused && !isFinishing) {
            ss!!.pause()
            paused = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moddedpe_main_pager)

        val fragment_list = ArrayList<Fragment>()
        val titles_list = ArrayList<CharSequence>()

        val startFragment = MainStartFragment()
        fragment_list.add(startFragment)
        titles_list.add(getString(R.string.main_title))

        mManageNModFragment = MainManageNModFragment()
        fragment_list.add(mManageNModFragment!!)
        titles_list.add(getString(R.string.manage_nmod_title))

        mMainSettingsFragment = MainSettingsFragment()
        fragment_list.add(mMainSettingsFragment!!)
        titles_list.add(getString(R.string.settings_title))

        val pagerAdapter = MainFragmentPagerAdapter(fragment_list, titles_list)

        mMainViewPager = findViewById<View>(R.id.moddedpe_main_view_pager) as ViewPager
        mMainViewPager!!.adapter = pagerAdapter
        mMainViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p1: Int, p2: Float, p3: Int) {
                title = mMainViewPager!!.adapter!!.getPageTitle(p1)!!
            }

            override fun onPageSelected(p1: Int) {

            }

            override fun onPageScrollStateChanged(p1: Int) {

            }
        })

        sc = object : ServiceConnection {
            override fun onServiceConnected(p1: ComponentName, p2: IBinder) {
                bound = true
                ss = (p2 as SoundService.SoundBinder).service
            }

            override fun onServiceDisconnected(p1: ComponentName) {
                bound = false
            }
        }

        if (!bound && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("background_music", true)) {
            bind()
        }
    }


    override fun onStop() {
        super.onStop()
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbind()
        }
    }

    override fun setDefaultActionBar() {
        super.setDefaultActionBar()

        val burgerButton = layoutInflater.inflate(R.layout.moddedpe_ui_button_menu, null)
        burgerButton.findViewById<View>(R.id.moddedpe_ui_button_item_image_button).setOnClickListener {
            val popup = PopupMenu(this@MainActivity, burgerButton)
            popup.menuInflater.inflate(R.menu.moddedpe_main_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                switchViewPager(item)
                true
            }
            popup.show()
        }
        setActionBarViewRight(burgerButton)
    }

    private fun switchViewPager(item: MenuItem) {
        when (item.itemId) {
            R.id.item_main_page -> mMainViewPager!!.setCurrentItem(0, false)
            R.id.item_manage_nmods -> mMainViewPager!!.setCurrentItem(1, false)
            R.id.item_launcher_settings -> mMainViewPager!!.setCurrentItem(2, false)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mManageNModFragment!!.onActivityResult(requestCode, resultCode, data)
        mMainSettingsFragment!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()

        val errorString = UtilsSettings(this).openGameFailed
        if (errorString != null) {
            AlertDialog.Builder(this).setTitle(R.string.launch_failed_title).setMessage(getString(R.string.launch_failed_message, *arrayOf<Any>(errorString))).setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }.show()
            UtilsSettings(this).openGameFailed = null
        }
        play()
    }

    private inner class MainFragmentPagerAdapter internal constructor(private val mFragments: List<Fragment>, private val mTitles: List<CharSequence>) : FragmentPagerAdapter(supportFragmentManager) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getItem(p1: Int): Fragment {
            return mFragments[p1]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }
    }
}
