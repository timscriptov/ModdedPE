package com.microsoft.xbox.toolkit.ui;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ButtonStateHandler {
    protected boolean disabled = false;
    protected boolean pressed = false;
    private XLEBitmap.XLEBitmapDrawable disabledImage = null;
    private int disabledImageHandle = -1;
    private XLEBitmap.XLEBitmapDrawable enabledImage = null;
    private int enabledImageHandle = -1;
    private XLEBitmap.XLEBitmapDrawable pressedImage = null;
    private int pressedImageHandle = -1;
    private ButtonStateHandlerRunnable pressedStateRunnable = null;

    public void setDisabledImageHandle(int imageHandle) {
        disabledImageHandle = imageHandle;
    }

    public void setEnabledImageHandle(int imageHandle) {
        enabledImageHandle = imageHandle;
    }

    public void setPressedImageHandle(int imageHandle) {
        pressedImageHandle = imageHandle;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled2) {
        disabled = disabled2;
    }

    public void setEnabled(boolean enabled) {
        disabled = !enabled;
    }

    public boolean onTouch(@NotNull MotionEvent event) {
        boolean oldpressed = pressed;
        if (event.getAction() == 0) {
            pressed = true;
        } else if (event.getAction() == 1) {
            pressed = false;
        } else if (event.getAction() == 3) {
            pressed = false;
        }
        if (!(pressedStateRunnable == null || oldpressed == pressed)) {
            pressedStateRunnable.onPressStateChanged(pressed);
        }
        return false;
    }

    public boolean onSizeChanged(int width, int height) {
        boolean loadedNewImage = false;
        if (disabledImage == null && disabledImageHandle != -1) {
            loadedNewImage = true;
            disabledImage = TextureManager.Instance().loadScaledResourceDrawable(disabledImageHandle);
        }
        if (enabledImage == null && enabledImageHandle != -1) {
            loadedNewImage = true;
            enabledImage = TextureManager.Instance().loadScaledResourceDrawable(enabledImageHandle);
        }
        if (pressedImage != null || pressedImageHandle == -1) {
            return loadedNewImage;
        }
        pressedImage = TextureManager.Instance().loadScaledResourceDrawable(pressedImageHandle);
        return true;
    }

    public Drawable getImageDrawable() {
        if (!pressed || pressedImageHandle == -1) {
            if (!disabled || disabledImageHandle == -1) {
                if (enabledImageHandle == -1 || enabledImage == null) {
                    return null;
                }
                return enabledImage.getDrawable();
            } else if (disabledImage != null) {
                return disabledImage.getDrawable();
            } else {
                return null;
            }
        } else if (pressedImage == null) {
            return null;
        } else {
            return pressedImage.getDrawable();
        }
    }

    public void setPressedStateRunnable(ButtonStateHandlerRunnable runnable) {
        pressedStateRunnable = runnable;
    }

    public interface ButtonStateHandlerRunnable {
        void onPressStateChanged(boolean z);
    }
}
