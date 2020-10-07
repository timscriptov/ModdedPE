package com.microsoft.xbox.toolkit.anim;

import android.annotation.SuppressLint;

import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MAAS {
    private static MAAS instance = new MAAS();
    private final String ASSET_FILENAME = "animation/%sAnimation.xml";
    @SuppressLint("SdCardPath")
    private final String SDCARD_FILENAME = "/sdcard/bishop/maas/%sAnimation.xml";
    private Hashtable<String, MAASAnimation> maasFileCache = new Hashtable<>();
    private boolean usingSdcard = false;

    public static MAAS getInstance() {
        return instance;
    }

    public MAASAnimation getAnimation(String name) {
        if (name != null) {
            return getMAASFile(name);
        }
        throw new IllegalArgumentException();
    }

    private MAASAnimation getMAASFile(String name) {
        MAASAnimation file;
        if (!maasFileCache.containsKey(name) && (file = loadMAASFile(name)) != null) {
            maasFileCache.put(name, file);
        }
        return maasFileCache.get(name);
    }

    @SuppressLint("SdCardPath")
    @Nullable
    private MAASAnimation loadMAASFile(String name) {
        InputStream s;
        try {
            if (usingSdcard) {
                s = new FileInputStream(new File(String.format("/sdcard/bishop/maas/%sAnimation.xml", new Object[]{name})));
            } else {
                s = XboxTcuiSdk.getAssetManager().open(String.format("animation/%sAnimation.xml", new Object[]{name}));
            }
            return XMLHelper.instance().load(s, MAASAnimation.class);
        } catch (Exception e) {
            return null;
        }
    }

    public enum MAASAnimationType {
        ANIMATE_IN,
        ANIMATE_OUT
    }
}
