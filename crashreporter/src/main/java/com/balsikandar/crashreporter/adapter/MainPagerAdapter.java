package com.balsikandar.crashreporter.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.balsikandar.crashreporter.ui.CrashLogFragment;
import com.balsikandar.crashreporter.ui.ExceptionLogFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Created by bali on 11/08/17.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;
    private CrashLogFragment crashLogFragment;
    private ExceptionLogFragment exceptionLogFragment;

    public MainPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public @NotNull Fragment getItem(int position) {
        if (position == 0) {
            return crashLogFragment = new CrashLogFragment();
        } else if (position == 1) {
            return exceptionLogFragment = new ExceptionLogFragment();
        } else {
            return new CrashLogFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void clearLogs() {
        crashLogFragment.clearLog();
        exceptionLogFragment.clearLog();
    }
}