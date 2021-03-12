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
package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.PreloadException;
import com.mcal.pesdk.Preloader;
import com.mcal.pesdk.nmod.NMod;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class PreloadActivity extends BaseActivity {
    private final static int MSG_START_MINECRAFT = 1;
    private final static int MSG_WRITE_TEXT = 2;
    private final static int MSG_ERROR = 3;
    private final static int MSG_START_NMOD_LOADING_FAILED = 4;
    private final PreloadUIHandler mPreloadUIHandler = new PreloadUIHandler();
    private LinearLayout mPreloadingMessageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_preloading);

        AppCompatTextView tipsText = findViewById(R.id.moddedpe_preloading_text);
        String[] tipsArray = getResources().getStringArray(R.array.preloading_tips_text);
        tipsText.setText(tipsArray[new Random().nextInt(tipsArray.length)]);

        mPreloadingMessageLayout = findViewById(R.id.moddedpe_preloading_texts_adapted_layput);

        new PreloadThread().start();
    }

    private void writeNewText(String text) {
        Message message = new Message();
        message.obj = text;
        message.what = MSG_WRITE_TEXT;
        mPreloadUIHandler.sendMessage(message);
    }

    private class PreloadThread extends Thread {
        private final ArrayList<NMod> mFailedNMods = new ArrayList<>();

        @Override
        public void run() {
            super.run();
            try {
                new Preloader(getPESdk(), null, new Preloader.PreloadListener() {
                    @Override
                    public void onStart() {
                        writeNewText(getString(R.string.preloading_initing));
                        if (Preferences.isSafeMode())
                            writeNewText(getString(R.string.preloading_initing_info_safe_mode, new Object[]{getPESdk().getMinecraftInfo().getMinecraftVersionName()}));
                        else
                            writeNewText(getString(R.string.preloading_initing_info, new Object[]{getPESdk().getNModAPI().getVersionName(), getPESdk().getMinecraftInfo().getMinecraftVersionName()}));
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs));
                    }

                    @Override
                    public void onLoadSubstrateLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_substrate));
                    }

                    @Override
                    public void onLoadFModLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_fmod));
                    }

                    @Override
                    public void onLoadXHookLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_xhook));
                    }

                    //@Override
                    //public void onLoadXHookSkyColorLib() {
                    //    writeNewText(getString(R.string.preloading_loading_lib_xhook_skycolor));
                    //}

                    @Override
                    public void onLoadMinecraftPELib() {
                        writeNewText(getString(R.string.preloading_loading_lib_minecraftpe));
                    }

                    @Override
                    public void onLoadPESdkLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_game_launcher));
                    }

                    @Override
                    public void onFinishedLoadingNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs_done));
                    }

                    @Override
                    public void onStartLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_start_loading));
                    }

                    @Override
                    public void onFinishedLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_finish_loading));
                    }

                    @Override
                    public void onNModLoaded(NMod nmod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded, new Object[]{nmod.getPackageName()}));
                    }

                    @Override
                    public void onFailedLoadingNMod(NMod nmod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded_failed, new Object[]{nmod.getPackageName()}));
                        mFailedNMods.add(nmod);
                    }

                    @Override
                    public void onFinish(Bundle bundle) {
                        if (mFailedNMods.isEmpty()) {
                            writeNewText(getString(R.string.preloading_finished));
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = MSG_START_MINECRAFT;
                            message.setData(bundle);
                            mPreloadUIHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = MSG_START_NMOD_LOADING_FAILED;
                            message.obj = mFailedNMods;
                            message.setData(bundle);
                            mPreloadUIHandler.sendMessage(message);
                        }
                    }
                }).preload(PreloadActivity.this);
            } catch (PreloadException e) {
                Message message = new Message();
                message.what = MSG_ERROR;
                message.obj = e;
                mPreloadUIHandler.sendMessage(message);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class PreloadUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WRITE_TEXT) {
                AppCompatTextView textView = (AppCompatTextView) getLayoutInflater().inflate(R.layout.moddedpe_ui_text_small, null);
                textView.setText((CharSequence) msg.obj);
                mPreloadingMessageLayout.addView(textView);
            } else if (msg.what == MSG_START_MINECRAFT) {
                Intent intent = new Intent(PreloadActivity.this, MinecraftActivity.class);
                intent.putExtras(msg.getData());
                startActivity(intent);
                finish();
            } else if (msg.what == MSG_ERROR) {
                PreloadException preloadException = (PreloadException) msg.obj;
                Preferences.setOpenGameFailed(preloadException.toString());
                Intent intent = new Intent(PreloadActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (msg.what == MSG_START_NMOD_LOADING_FAILED) {
                NModLoadFailActivity.startThisActivity(PreloadActivity.this, (ArrayList<NMod>) msg.obj, msg.getData());
                finish();
            }
        }
    }
}