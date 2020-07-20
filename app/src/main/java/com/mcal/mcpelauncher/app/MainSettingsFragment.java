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
package com.mcal.mcpelauncher.app;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.snackbar.Snackbar;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.utils.I18n;
import com.mcal.mcpelauncher.utils.UtilsSettings;
import com.mcal.pesdk.utils.LauncherOptions;

import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MainSettingsFragment extends PreferenceFragmentCompat {
    private UtilsSettings mSettings;
    private Preference mDataPathPreference;
    private Preference mPkgPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        mSettings = new UtilsSettings(getActivity());

        SwitchPreference mBackgroundMusicPreference = (SwitchPreference) findPreference("background_music");
        mBackgroundMusicPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference p1, Object p2) {
                if ((boolean) p2) {
                    ((BackgroundSoundPlayer) getActivity()).bind();
                } else ((BackgroundSoundPlayer) getActivity()).unbind();
                return true;
            }
        });

        SwitchPreference mDesktopGuiPreference = (SwitchPreference) findPreference("desktop_gui");
        mDesktopGuiPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference p1, Object p2) {
                File optionsFileDir = new File(Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftpe");
                File optionsFile = new File(optionsFileDir + "/options.txt");

                if (!optionsFileDir.exists()) {
                    optionsFileDir.mkdirs();
                }

                if (!optionsFile.exists()) {
                    try {
                        FileWriter writer = new FileWriter(optionsFile);
                        writer.write("gfx_guiscale_offset:0");
                        writer.flush();
                    } catch (IOException ignored) {
                    }
                }

                String fileContent = null;
                try {
                    fileContent = FileUtils.readFileToString(optionsFile);
                } catch (IOException ignored) {
                }

                if ((boolean) p2) {
                    try {
                        FileWriter writer = new FileWriter(optionsFile);
                        writer.write(fileContent.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)", "gfx_guiscale_offset:-1"));
                        writer.flush();
                    } catch (IOException e) {
                    }
                } else {
                    try {
                        FileWriter writer = new FileWriter(optionsFile);
                        writer.write(fileContent.replaceFirst("gfx_guiscale_offset:(\\d|-\\d)", "gfx_guiscale_offset:0"));
                        writer.flush();
                    } catch (IOException e) {
                    }
                }
                return true;
            }
        });
        
            /*SwitchPreference mXboxPreference = (SwitchPreference) findPreference("xbox");
            mXboxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference p1, Object p2) {
                                File dirData = new File("/data/data/com.mcal.mcpelauncher/");
                                File dirUser = new File("/data/user/0/com.mcal.mcpelauncher/");
                                
                                if ((boolean) p2) {
                                        if (!dirData.exists()) {
                                                dirData.mkdirs();
                                                try {
                                                        ZipUtil.unpack(getActivity().getAssets().open("xbox.zip"), dirData);
                                                    } catch (IOException e) {
                                                    }
                                            } else {
                                                try {
                                                        FileUtils.deleteDirectory(dirData);
                                                        dirData.delete();
                                                    } catch (IOException e) {
                                                    }
                                            }
                                            
                                            dirUser.mkdirs();
                                            try {
                                                ZipUtil.unpack(getActivity().getAssets().open("xbox.zip"), dirUser);
                                            } catch (IOException e) {
                                            }
                                    } else {
                                        try {
                                                FileUtils.deleteDirectory(dirUser);
                                                dirUser.delete();
                                            } catch (IOException e) {
                                            }
                                    }
                                return true;
                            }
                    });
                    
            SwitchPreference mAddServersPreference = (SwitchPreference) findPreference("servers");
            mAddServersPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference p1, Object p2) {
                                File dirData= new File("/data/data/com.mcal.mcpelauncher/files/com.mojang.minecraftpe/games/com.mojang/minecraftpe/");
                                File dirUser = new File("/data/user/0/com.mcal.mcpelauncher/files/com.mojang.minecraftpe/games/com.mojang/minecraftpe/");
                                
                                if ((boolean) p2) {
                                        if (!dirData.exists()) {
                                                dirData.mkdirs();
                                                try {
                                                        ZipUtil.unpack(getActivity().getAssets().open("servers.zip"), dirData);
                                                    } catch (IOException e) {
                                                    }
                                            } else {
                                                try {
                                                        FileUtils.deleteDirectory(dirData);
                                                        dirData.delete();
                                                    } catch (IOException e) {
                                                    }
                                            } dirUser.mkdirs();
                                            
                                        try {
                                                ZipUtil.unpack(getActivity().getAssets().open("servers.zip"), dirUser);
                                            } catch (IOException e) {
                                            }
                                    } else {
                                        try {
                                                FileUtils.deleteDirectory(dirUser);
                                                dirUser.delete();
                                            } catch (IOException e) {
                                            }
                                    }
                                return true;
                            }
                    });*/

        SwitchPreference mModdedPEPackPreference = (SwitchPreference) findPreference("resource_pack");
        mModdedPEPackPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference p1, Object p2) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/games/com.mojang/resource_packs/ModdedPE");
                File gsonFileDir = new File(Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftpe");
                File gsonFile = new File(Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftpe/global_resource_packs.json");

                if ((boolean) p2) {
                    if (!dir.exists()) {
                        dir.mkdirs();
                        try {
                            ZipUtil.unpack(getActivity().getAssets().open("resource_pack.zip"), dir);
                        } catch (IOException e) {
                        }
                    } else {
                        try {
                            FileUtils.deleteDirectory(dir);
                            dir.delete();
                        } catch (IOException e) {
                        }
                    }

                    gsonFileDir.mkdirs();
                    try {
                        InputStream is = getActivity().getAssets().open("global_resource_packs.json");
                        FileWriter fw = new FileWriter(gsonFileDir + "/global_resource_packs.json");
                        InputStreamReader isr = new InputStreamReader(new BufferedInputStream(is));
                        BufferedReader br = new BufferedReader(isr);

                        String line;
                        while ((line = br.readLine()) != null) {
                            fw.write(line + "\n");
                        }
                        fw.flush();
                        fw.close();
                        br.close();
                    } catch (IOException e) {
                    }
                } else {
                    try {
                        FileUtils.deleteDirectory(dir);
                        gsonFile.delete();
                    } catch (IOException e) {
                    }
                }
                return true;
            }
        });

        SwitchPreference mSafeModePreference = (SwitchPreference) findPreference("safe_mode");
        mSafeModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference p1, Object p2) {
                mSettings.setSafeMode((boolean) p2);
                return true;
            }


        });
        mSafeModePreference.setChecked(mSettings.isSafeMode());

        mDataPathPreference = findPreference("data_saved_path");
        mDataPathPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference p1) {
                if (checkPermissions())
                    DirPickerActivity.startThisActivity(getActivity());
                return true;
            }


        });
        Preference mAboutPreference = findPreference("about");
        mAboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference p1) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
                return true;
            }


        });
        mPkgPreference = findPreference("game_pkg_name");
        mPkgPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference p1) {
                MCPkgPickerActivity.startThisActivity(getActivity());
                return true;
            }


        });

        ListPreference mLanguagePreference = (ListPreference) findPreference("language");
        mLanguagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference p1, Object p2) {
                int type = Integer.valueOf((String) p2);
                mSettings.setLanguageType(type);
                I18n.setLanguage(getActivity());
                Intent intent = new Intent(getActivity(), SplashesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }


        });
        updatePreferences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DirPickerActivity.REQUEST_PICK_DIR && resultCode == Activity.RESULT_OK) {
            String dir = data.getExtras().getString(DirPickerActivity.TAG_DIR_PATH);
            mSettings.setDataSavedPath(dir);
            if (dir.equals(LauncherOptions.STRING_VALUE_DEFAULT))
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_reset_data_path), 2500).show();
            else
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_data_path, new Object[]{dir}), 2500).show();
        } else if (requestCode == MCPkgPickerActivity.REQUEST_PICK_PACKAGE && resultCode == Activity.RESULT_OK) {
            String pkgName = data.getExtras().getString(MCPkgPickerActivity.TAG_PACKAGE_NAME);
            mSettings.setMinecraftPackageName(pkgName);
            if (pkgName.equals(LauncherOptions.STRING_VALUE_DEFAULT))
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_reset_pkg_name), 2500).show();
            else
                Snackbar.make(getActivity().getWindow().getDecorView(), getString(R.string.preferences_update_message_pkg_name, new Object[]{pkgName}), 2500).show();
            Intent intent = new Intent(getActivity(), SplashesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
        updatePreferences();
    }

    private void updatePreferences() {
        if (!mSettings.getDataSavedPath().equals(mSettings.STRING_VALUE_DEFAULT))
            mDataPathPreference.setSummary(mSettings.getDataSavedPath());
        else
            mDataPathPreference.setSummary(R.string.preferences_summary_data_saved_path);

        if (!mSettings.getMinecraftPEPackageName().equals(mSettings.STRING_VALUE_DEFAULT))
            mPkgPreference.setSummary(mSettings.getMinecraftPEPackageName());
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
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                DirPickerActivity.startThisActivity(getActivity());
            } else {
                showPermissionDinedDialog();
            }
        }
    }
}
