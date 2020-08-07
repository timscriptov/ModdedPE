package com.mojang.minecraftpe;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.appcompat.widget.AppCompatEditText;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TextInputProxyEditTextbox extends AppCompatEditText {
    public MCPEKeyWatcher _mcpeKeyWatcher;
    public int allowedLength;
    private String mLastSentText;

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _mcpeKeyWatcher = null;
        allowedLength = 160;
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        _mcpeKeyWatcher = null;
        allowedLength = 160;
    }

    public TextInputProxyEditTextbox(Context context) {
        super(context);
        _mcpeKeyWatcher = null;
    }

    public void updateFilters(int allowedLength2, boolean singleLine) {
        this.allowedLength = allowedLength2;
        ArrayList<InputFilter> filterList = new ArrayList<>();
        if (allowedLength2 != 0) {
            filterList.add(new InputFilter.LengthFilter(allowedLength));
        }
        if (singleLine) {
            filterList.add(createSingleLineFilter());
        }
        filterList.add(createUnicodeFilter());
        setFilters(filterList.toArray(new InputFilter[filterList.size()]));
    }

    public boolean shouldSendText() {
        return mLastSentText == null || !getText().toString().equals(mLastSentText);
    }

    public void setTextFromGame(String text) {
        mLastSentText = new String(text);
        setText(text);
    }

    public void updateLastSentText() {
        mLastSentText = new String(getText().toString());
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MCPEInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 1) {
            return super.onKeyPreIme(keyCode, event);
        }
        if (_mcpeKeyWatcher != null) {
            return _mcpeKeyWatcher.onBackKeyPressed();
        }
        return false;
    }

    public void setOnMCPEKeyWatcher(MCPEKeyWatcher mcpeKeyWatcher) {
        this._mcpeKeyWatcher = mcpeKeyWatcher;
    }

    @NotNull
    @Contract(pure = true)
    private InputFilter createSingleLineFilter() {
        return (source, start, end, dest, destStart, destEnd) -> {
            for (int i = start; i < end; i++) {
                if (source.charAt(i) == 10) {
                    return dest.subSequence(destStart, destEnd);
                }
            }
            return null;
        };
    }

    @NotNull
    @Contract(pure = true)
    private InputFilter createUnicodeFilter() {
        return (source, start, end, dest, destStart, destEnd) -> {
            StringBuilder modString = null;
            for (int i = start; i < end; i++) {
                if (source.charAt(i) == 12288) {
                    if (modString == null) {
                        modString = new StringBuilder(source);
                    }
                    modString.setCharAt(i, ' ');
                }
            }
            if (modString != null) {
                return modString.subSequence(start, end);
            }
            return null;
        };
    }

    public interface MCPEKeyWatcher {
        boolean onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    private class MCPEInputConnection extends InputConnectionWrapper {
        TextInputProxyEditTextbox textbox;

        public MCPEInputConnection(InputConnection target, boolean mutable, TextInputProxyEditTextbox textbox2) {
            super(target, mutable);
            textbox = textbox2;
        }

        public boolean sendKeyEvent(KeyEvent event) {
            if (textbox.getText().length() != 0 || event.getAction() != 0 || event.getKeyCode() != 67) {
                return super.sendKeyEvent(event);
            }
            if (_mcpeKeyWatcher != null) {
                _mcpeKeyWatcher.onDeleteKeyPressed();
            }
            return false;
        }
    }
}