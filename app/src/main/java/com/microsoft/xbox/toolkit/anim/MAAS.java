package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MAAS {
    private static MAAS instance = new MAAS();
    private final String ASSET_FILENAME = "animation/%sAnimation.xml";
    private final String SDCARD_FILENAME = "/sdcard/bishop/maas/%sAnimation.xml";
    private Hashtable<String, MAASAnimation> maasFileCache = new Hashtable<>();
    private boolean usingSdcard = false;

    public static MAAS getInstance() {
        return instance;
    }

    public MAASAnimation getAnimation(String str) {
        if (str != null) {
            return getMAASFile(str);
        }
        throw new IllegalArgumentException();
    }

    private MAASAnimation getMAASFile(String str) {
        MAASAnimation loadMAASFile;
        if (!this.maasFileCache.containsKey(str) && (loadMAASFile = loadMAASFile(str)) != null) {
            this.maasFileCache.put(str, loadMAASFile);
        }
        return this.maasFileCache.get(str);
    }

    private @Nullable MAASAnimation loadMAASFile(String str) {
        InputStream inputStream;
        try {
            if (this.usingSdcard) {
                inputStream = new FileInputStream(new File(String.format("/sdcard/bishop/maas/%sAnimation.xml", new Object[]{str})));
            } else {
                inputStream = XboxTcuiSdk.getAssetManager().open(String.format("animation/%sAnimation.xml", new Object[]{str}));
            }
            return (MAASAnimation) XMLHelper.instance().load(inputStream, MAASAnimation.class);
        } catch (Exception unused) {
            return null;
        }
    }

    public enum MAASAnimationType {
        ANIMATE_IN,
        ANIMATE_OUT
    }
}
