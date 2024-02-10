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

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class TextInputProxyEditTextbox extends AppCompatEditText {
    public int allowedLength;
    private MCPEKeyWatcher _mcpeKeyWatcher;
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
        return mLastSentText == null || !getText().toString().equals(mLastSentText);
    }

    public void setTextFromGame(String text) {
        this.mLastSentText = text;
        setText(text);
    }

    public void updateLastSentText() {
        mLastSentText = getText().toString();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MCPEInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getAction() == KeyEvent.ACTION_UP) {
            MCPEKeyWatcher mCPEKeyWatcher = _mcpeKeyWatcher;
            if (mCPEKeyWatcher == null) {
                return false;
            }
            return mCPEKeyWatcher.onBackKeyPressed();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnMCPEKeyWatcher(MCPEKeyWatcher mcpeKeyWatcher) {
        _mcpeKeyWatcher = mcpeKeyWatcher;
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

    public interface MCPEKeyWatcher {
        boolean onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    private class MCPEInputConnection extends InputConnectionWrapper {
        TextInputProxyEditTextbox textbox;

        public MCPEInputConnection(InputConnection target, boolean mutable, TextInputProxyEditTextbox textbox) {
            super(target, mutable);
            this.textbox = textbox;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (textbox.getText().length() == 0 && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 67) {
                if (_mcpeKeyWatcher == null) {
                    return false;
                }
                _mcpeKeyWatcher.onDeleteKeyPressed();
                return false;
            }
            return super.sendKeyEvent(event);
        }
    }
}
