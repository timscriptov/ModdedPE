/*
 * Copyright (C) 2018-2020 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcal.mcpelauncher.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.snackbar.Snackbar;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.activities.AboutActivity;
import com.mcal.mcpelauncher.activities.DirPickerActivity;
import com.mcal.mcpelauncher.activities.MCPkgPickerActivity;
import com.mcal.mcpelauncher.activities.SplashesActivity;
import com.mcal.mcpelauncher.data.Constants;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.mcpelauncher.services.BackgroundSoundPlayer;
import com.mcal.mcpelauncher.utils.DesktopGui;
import com.mcal.mcpelauncher.utils.I18n;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MainSettingsFragment extends PreferenceFragmentCompat {
    private Preference mDataPathPreference;
    private Preference mPkgPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        SwitchPreference mBackgroundMusicPreference = findPreference("background_music");
        mBackgroundMusicPreference.setOnPreferenceChangeListener((p1, p2) -> {
            if ((boolean) p2) {
                ((BackgroundSoundPlayer) getActivity()).bind();
            } else ((BackgroundSoundPlayer) getActivity()).unbind();
            return true;
        });

        SwitchPreference mDesktopGuiPreference = findPreference("desktop_gui");
        mDesktopGuiPreference.setOnPreferenceChangeListener((p1, p2) -> {
            DesktopGui.run(getContext());
            return true;
        });

        SwitchPreference mWebViewCorePreference = findPreference("webview_engine");
        mWebViewCorePreference.setOnPreferenceChangeListener((p1, p2) -> {
            if(Build.VERSION.SDK_INT >= 24) {
                Intent intent = new Intent(Settings.ACTION_WEBVIEW_SETTINGS);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            } else {
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.not_supported_on_your_device), 2500).show();
            }
            return true;
        });

        SwitchPreference mSafeModePreference = findPreference("safe_mode");
        mSafeModePreference.setOnPreferenceChangeListener((p1, p2) -> {
            Preferences.setSafeMode((boolean) p2);
            return true;
        });
        mSafeModePreference.setChecked(Preferences.isSafeMode());

        mDataPathPreference = findPreference("data_saved_path");
        mDataPathPreference.setOnPreferenceClickListener(p1 -> {
            if (checkPermissions())
                DirPickerActivity.startThisActivity((AppCompatActivity) getActivity());
            return true;
        });

        Preference mAboutPreference = findPreference("about");
        mAboutPreference.setOnPreferenceClickListener(p1 -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            getActivity().startActivity(intent);
            return true;
        });

        mPkgPreference = findPreference("game_pkg_name");
        mPkgPreference.setOnPreferenceClickListener(p1 -> {
            MCPkgPickerActivity.startThisActivity((AppCompatActivity) getActivity());
            return true;
        });

        SwitchPreference mNightModeePreference = findPreference("night_mode");
        mNightModeePreference.setOnPreferenceChangeListener((p1, p2) -> {
            if (Preferences.isNightMode()) {
                Preferences.setNightMode(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                restartPerfect(requireActivity().getIntent());
            } else {
                Preferences.setNightMode(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                restartPerfect(requireActivity().getIntent());
            }
            return true;
        });
        mNightModeePreference.setChecked(Preferences.isNightMode());

        ListPreference mLanguagePreference = findPreference("language");
        mLanguagePreference.setOnPreferenceChangeListener((p1, p2) -> {
            int type = Integer.parseInt((String) p2);
            Preferences.setLanguageType(type);
            I18n.setLanguage(getActivity());
            Intent intent = new Intent(getActivity(), SplashesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        });
        updatePreferences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DirPickerActivity.REQUEST_PICK_DIR && resultCode == AppCompatActivity.RESULT_OK) {
            String dir = data.getExtras().getString(DirPickerActivity.TAG_DIR_PATH);
            Preferences.setDataSavedPath(dir);
            if (dir.equals(Constants.STRING_VALUE_DEFAULT))
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_reset_data_path), 2500).show();
            else
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_data_path, dir), 2500).show();
        } else if (requestCode == MCPkgPickerActivity.REQUEST_PICK_PACKAGE && resultCode == AppCompatActivity.RESULT_OK) {
            String pkgName = data.getExtras().getString("package_name");
            Preferences.setMinecraftPackageName(pkgName);
            if (pkgName.equals(Constants.STRING_VALUE_DEFAULT))
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_reset_pkg_name), 2500).show();
            else
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_pkg_name, pkgName), 2500).show();
            Intent intent = new Intent(getActivity(), SplashesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
        updatePreferences();
    }

    private void updatePreferences() {
        if (!Preferences.getDataSavedPath().equals("default"))
            mDataPathPreference.setSummary(Preferences.getDataSavedPath());
        else
            mDataPathPreference.setSummary(R.string.preferences_summary_data_saved_path);

        if (!Preferences.getMinecraftPEPackageName().equals("com.mojang.minecraftpe"))
            mPkgPreference.setSummary(Preferences.getMinecraftPEPackageName());
        else
            mPkgPreference.setSummary(R.string.preferences_summary_game_pkg_name);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    private void showPermissionDinedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.permission_grant_failed_title);
        builder.setMessage(R.string.permission_grant_failed_message);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean isAllGranted = true;

            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                DirPickerActivity.startThisActivity((AppCompatActivity) getActivity());
            } else {
                showPermissionDinedDialog();
            }
        }
    }

    private void restartPerfect(Intent intent) {
        requireActivity().finish();
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }
}