package com.mojang.minecraftpe;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class TextInputProxyEditTextbox extends AppCompatEditText {
    private MCPEKeyWatcher _mcpeKeyWatcher;
    public int allowedLength;
    private String mLastSentText;

    public interface MCPEKeyWatcher {
        boolean onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._mcpeKeyWatcher = null;
        this.allowedLength = 160;
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._mcpeKeyWatcher = null;
        this.allowedLength = 160;
    }

    public TextInputProxyEditTextbox(Context context) {
        super(context);
        this._mcpeKeyWatcher = null;
    }

    public void updateFilters(int allowedLength, boolean singleLine) {
        this.allowedLength = allowedLength;
        ArrayList<InputFilter> arrayList = new ArrayList<>();
        if (allowedLength != 0) {
            arrayList.add(new InputFilter.LengthFilter(this.allowedLength));
        }
        if (singleLine) {
            arrayList.add(createSingleLineFilter());
        }
        arrayList.add(createUnicodeFilter());
        setFilters(arrayList.toArray(new InputFilter[arrayList.size()]));
    }

    public boolean shouldSendText() {
        return this.mLastSentText == null || !getText().toString().equals(this.mLastSentText);
    }

    public void setTextFromGame(String text) {
        this.mLastSentText = text;
        setText(text);
    }

    public void updateLastSentText() {
        this.mLastSentText = getText().toString();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MCPEInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getAction() == 1) {
            MCPEKeyWatcher mCPEKeyWatcher = this._mcpeKeyWatcher;
            if (mCPEKeyWatcher == null) {
                return false;
            }
            return mCPEKeyWatcher.onBackKeyPressed();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnMCPEKeyWatcher(MCPEKeyWatcher mcpeKeyWatcher) {
        this._mcpeKeyWatcher = mcpeKeyWatcher;
    }

    @NonNull
    @Contract(pure = true)
    private InputFilter createSingleLineFilter() {
        return (source, start, end, dest, destStart, destEnd) -> {
            while (start < end) {
                if (source.charAt(start) == '\n') {
                    return dest.subSequence(destStart, destEnd);
                }
                start++;
            }
            return null;
        };
    }

    @NonNull
    @Contract(pure = true)
    private InputFilter createUnicodeFilter() {
        return (source, start, end, dest, destStart, destEnd) -> {
            StringBuilder sb = null;
            for (int i = start; i < end; i++) {
                if (source.charAt(i) == 12288) {
                    if (sb == null) {
                        sb = new StringBuilder(source);
                    }
                    sb.setCharAt(i, ' ');
                }
            }
            if (sb != null) {
                return sb.subSequence(start, end);
            }
            return null;
        };
    }

    private class MCPEInputConnection extends InputConnectionWrapper {
        TextInputProxyEditTextbox textbox;

        public MCPEInputConnection(InputConnection target, boolean mutable, TextInputProxyEditTextbox textbox) {
            super(target, mutable);
            this.textbox = textbox;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (this.textbox.getText().length() == 0 && event.getAction() == 0 && event.getKeyCode() == 67) {
                if (TextInputProxyEditTextbox.this._mcpeKeyWatcher == null) {
                    return false;
                }
                TextInputProxyEditTextbox.this._mcpeKeyWatcher.onDeleteKeyPressed();
                return false;
            }
            return super.sendKeyEvent(event);
        }
    }
}