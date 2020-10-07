package com.microsoft.xbox.toolkit;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.microsoft.xboxtcui.XboxTcuiSdk;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SoundManager {
    private static final int MAX_STREAM_SIZE = 14;
    private static final int NO_LOOP = 0;
    private AudioManager audioManager;
    private Context context;
    private boolean isEnabled;
    private ArrayList<Integer> recentlyPlayedResourceIds;
    private HashMap<Integer, Integer> resourceSoundIdMap;
    private SoundPool soundPool;

    private SoundManager() {
        boolean z;
        resourceSoundIdMap = new HashMap<>();
        recentlyPlayedResourceIds = new ArrayList<>();
        isEnabled = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue("You must access sound manager on UI thread.", z);
        context = XboxTcuiSdk.getApplicationContext();
        soundPool = new SoundPool(14, 3, 0);
        audioManager = (AudioManager) context.getSystemService("audio");
    }

    public static SoundManager getInstance() {
        return SoundManagerHolder.instance;
    }

    public void setEnabled(boolean value) {
        if (isEnabled != value) {
            isEnabled = value;
        }
    }

    public void loadSound(int resId) {
        if (!resourceSoundIdMap.containsKey(Integer.valueOf(resId))) {
            resourceSoundIdMap.put(Integer.valueOf(resId), Integer.valueOf(soundPool.load(context, resId, 1)));
        }
    }

    public void playSound(int resId) {
        int soundId;
        if (isEnabled) {
            if (!resourceSoundIdMap.containsKey(Integer.valueOf(resId))) {
                soundId = soundPool.load(context, resId, 1);
                resourceSoundIdMap.put(Integer.valueOf(resId), Integer.valueOf(soundId));
            } else {
                soundId = resourceSoundIdMap.get(Integer.valueOf(resId)).intValue();
            }
            float volume = ((float) audioManager.getStreamVolume(3)) / ((float) audioManager.getStreamMaxVolume(3));
            soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
        }
    }

    public void clearMostRecentlyPlayedResourceIds() {
    }

    public Integer[] getMostRecentlyPlayedResourceIds() {
        return new Integer[0];
    }

    private static class SoundManagerHolder {
        public static final SoundManager instance = new SoundManager();

        private SoundManagerHolder() {
        }
    }
}
