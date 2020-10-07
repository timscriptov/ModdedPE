package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class ScreenModuleLayout extends FrameLayout {
    public ScreenModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract ViewModelBase getViewModel();

    public abstract void setViewModel(ViewModelBase viewModelBase);

    public abstract void updateView();

    public void setContentView(int screenLayoutId) {
        ((LayoutInflater) XboxTcuiSdk.getSystemService("layout_inflater")).inflate(screenLayoutId, this, true);
    }

    public void onPause() {
    }

    public void onApplicationPause() {
    }

    public void onApplicationResume() {
    }

    public void onResume() {
    }

    public void onDestroy() {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void invalidateView() {
    }
}
