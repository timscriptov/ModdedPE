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
package com.mcal.mcpelauncher.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.mcal.mcpelauncher.BuildConfig;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.activities.PreloadActivity;
import com.mcal.mcpelauncher.data.Preferences;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MainStartFragment extends BaseFragment {
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moddedpe_main, null);
        view.findViewById(R.id.moddedpe_main_play_button).setOnClickListener(p1 -> onPlayClicked());
        return view;
    }

    private void onPlayClicked() {
        if (!getPESdk().getMinecraftInfo().isMinecraftInstalled()) {
            AlertDialog.Builder mdialog = new AlertDialog.Builder(getActivity());
            mdialog.setTitle(getString(R.string.no_mcpe_found_title));
            mdialog.setMessage(getString(R.string.no_mcpe_found));
            mdialog.setPositiveButton(getString(android.R.string.cancel), (p1, id) -> p1.dismiss());
            mdialog.show();
        } else if (!getPESdk().getMinecraftInfo().isSupportedMinecraftVersion(getResources().getStringArray(R.array.target_mcpe_versions))) {
            AlertDialog.Builder mdialog = new AlertDialog.Builder(getActivity());
            mdialog.setTitle(getString(R.string.no_available_mcpe_version_found_title));
            mdialog.setMessage(getString(R.string.no_available_mcpe_version_found, getPESdk().getMinecraftInfo().getMinecraftVersionName(), R.string.app_game + " " + BuildConfig.VERSION_NAME));
            mdialog.setNegativeButton(getString(android.R.string.cancel), (p1, id) -> p1.dismiss());
            mdialog.setPositiveButton(getString(R.string.no_available_mcpe_version_continue), (p1, id) -> startMinecraft());
            mdialog.show();
        } else
            startMinecraft();
    }

    private void startMinecraft() {
        if (Preferences.isSafeMode()) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.safe_mode_on_title).setMessage(R.string.safe_mode_on_message).
                    setPositiveButton(android.R.string.ok, (p1, p2) -> {
                        Intent intent = new Intent(getActivity(), PreloadActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        p1.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (p1, p2) -> p1.dismiss()).show();
        } else {
            startActivity(new Intent(getActivity(), PreloadActivity.class));
            getActivity().finish();
        }
    }
}