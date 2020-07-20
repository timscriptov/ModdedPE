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
package com.mcal.mcpelauncher.app;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.utils.UtilsSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MainActivity extends BaseActivity implements BackgroundSoundPlayer {
    private ServiceConnection sc;
    private SoundService ss;
    private boolean bound, paused;
    private ViewPager mMainViewPager;
    private MainManageNModFragment mManageNModFragment;
    private MainSettingsFragment mMainSettingsFragment;

    @Override
    public void bind() {
        bindService(new Intent(this, SoundService.class), sc, BIND_AUTO_CREATE);
    }

    @Override
    public void unbind() {
        unbindService(sc);
    }

    @Override
    public void play() {
        if (bound && paused) {
            ss.play();
            paused = false;
        }
    }

    @Override
    public void pause() {
        if (bound && !paused && !isFinishing()) {
            ss.pause();
            paused = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_main_pager);

        ArrayList<Fragment> fragment_list = new ArrayList<>();
        ArrayList<CharSequence> titles_list = new ArrayList<>();

        MainStartFragment startFragment = new MainStartFragment();
        fragment_list.add(startFragment);
        titles_list.add(getString(R.string.main_title));

        mManageNModFragment = new MainManageNModFragment();
        fragment_list.add(mManageNModFragment);
        titles_list.add(getString(R.string.manage_nmod_title));

        mMainSettingsFragment = new MainSettingsFragment();
        fragment_list.add(mMainSettingsFragment);
        titles_list.add(getString(R.string.settings_title));

        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(fragment_list, titles_list);

        mMainViewPager = (ViewPager) findViewById(R.id.moddedpe_main_view_pager);
        mMainViewPager.setAdapter(pagerAdapter);
        mMainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int p1, float p2, int p3) {
                setTitle(mMainViewPager.getAdapter().getPageTitle(p1));
            }

            @Override
            public void onPageSelected(int p1) {

            }

            @Override
            public void onPageScrollStateChanged(int p1) {

            }
        });

        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName p1, IBinder p2) {
                bound = true;
                ss = ((SoundService.SoundBinder) p2).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName p1) {
                bound = false;
            }
        };

        if (!bound && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("background_music", false)) {
            bind();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbind();
        }
    }

    @Override
    protected void setDefaultActionBar() {
        super.setDefaultActionBar();

        final View burgerButton = getLayoutInflater().inflate(R.layout.moddedpe_ui_button_menu, null);
        burgerButton.findViewById(R.id.moddedpe_ui_button_item_image_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                PopupMenu popup = new PopupMenu(MainActivity.this, burgerButton);
                popup.getMenuInflater().inflate(R.menu.moddedpe_main_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switchViewPager(item);
                        return true;
                    }
                });
                popup.show();
            }
        });
        setActionBarViewRight(burgerButton);
    }

    private void switchViewPager(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_main_page:
                mMainViewPager.setCurrentItem(0, false);
                break;
            case R.id.item_manage_nmods:
                mMainViewPager.setCurrentItem(1, false);
                break;
            case R.id.item_launcher_settings:
                mMainViewPager.setCurrentItem(2, false);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mManageNModFragment.onActivityResult(requestCode, resultCode, data);
        mMainSettingsFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String errorString = new UtilsSettings(this).getOpenGameFailed();
        if (errorString != null) {
            new AlertDialog.Builder(this).setTitle(R.string.launch_failed_title).setMessage(getString(R.string.launch_failed_message, new Object[]{errorString})).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            new UtilsSettings(this).setOpenGameFailed(null);
        }
        play();
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        private List<CharSequence> mTitles;

        MainFragmentPagerAdapter(List<Fragment> fragments, List<CharSequence> titles) {
            super(getSupportFragmentManager());
            this.mFragments = fragments;
            this.mTitles = titles;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int p1) {
            return mFragments.get(p1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}
