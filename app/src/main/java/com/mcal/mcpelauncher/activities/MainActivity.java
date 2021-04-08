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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.mcpelauncher.fragments.MainManageNModFragment;
import com.mcal.mcpelauncher.fragments.MainSettingsFragment;
import com.mcal.mcpelauncher.fragments.MainStartFragment;
import com.mcal.mcpelauncher.services.BackgroundSoundPlayer;
import com.mcal.mcpelauncher.services.SoundService;
import com.mcal.mcpelauncher.ui.view.Dialogs;
import com.mcal.mcpelauncher.utils.ExceptionHandler;

import org.jetbrains.annotations.NotNull;

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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
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

        mMainViewPager = findViewById(R.id.moddedpe_main_view_pager);
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

        if (!bound && Preferences.isBackgroundMusic()) {
            bind();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            Dialogs.showScopedStorageDialog(this);
        }

        if (!Preferences.getRated()) {
            Dialogs.rate(this);
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
        burgerButton.findViewById(R.id.moddedpe_ui_button_item_image_button).setOnClickListener(p1 -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, burgerButton);
            popup.getMenuInflater().inflate(R.menu.moddedpe_main_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switchViewPager(item);
                return true;
            });
            popup.show();
        });
        setActionBarViewRight(burgerButton);
    }

    private void switchViewPager(@NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_main_page) {
            mMainViewPager.setCurrentItem(0, false);
        } else if (id == R.id.item_manage_nmods) {
            mMainViewPager.setCurrentItem(1, false);
        } else if (id == R.id.item_launcher_settings) {
            mMainViewPager.setCurrentItem(2, false);
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
        String errorString = Preferences.getOpenGameFailed();
        if (errorString != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            dialog.setTitle(R.string.launch_failed_title);
            dialog.setMessage(getString(R.string.launch_failed_message, errorString));
            dialog.setPositiveButton(android.R.string.ok, (dialog1, which) -> dialog1.dismiss());
            dialog.show();
            Preferences.setOpenGameFailed(null);
        }
        play();
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments;
        private final List<CharSequence> mTitles;

        MainFragmentPagerAdapter(List<Fragment> fragments, List<CharSequence> titles) {
            super(getSupportFragmentManager());
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public @NotNull Fragment getItem(int p1) {
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