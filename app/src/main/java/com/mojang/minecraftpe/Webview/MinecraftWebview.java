package com.mojang.minecraftpe.Webview;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.PopupView;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MinecraftWebview {
    private MainActivity mActivity = MainActivity.mInstance;
    public WebView mWebView;
    public PopupView mWebViewPopup;

    public native void nativeDismiss();

    public native void nativeOnWebError(int i, String str);

    public native void nativeSendToHost(String str);

    public MinecraftWebview() {
        this.mActivity.runOnUiThread(() -> _createWebView());
    }

    public void teardown() {
        mWebViewPopup.dismiss();
        mWebViewPopup = null;
        mWebView = null;
        mActivity = null;
    }

    public void setRect(float minX, float maxX, float minY, float maxY) {
        final int x0 = (int) minX;
        final int x1 = (int) maxX;
        final int y0 = (int) minY;
        final int y1 = (int) maxY;
        mActivity.runOnUiThread(() -> {
            mWebViewPopup.setRect(x0, x1, y0, y1);
            mWebViewPopup.update();
        });
    }

    public void setPropagatedAlpha(float alpha) {
        setShowView(((double) alpha) == 1.0d);
    }

    public void setUrl(String url) {
        final String urlCapture = url;
        mActivity.runOnUiThread(() -> mWebView.loadUrl(urlCapture));
    }

    public void setShowView(boolean show) {
        final boolean showCapture = show;
        mActivity.runOnUiThread(() -> mWebViewPopup.setVisible(showCapture));
    }

    public void sendToWebView(String data) {
        final String toEvaluate = data;
        mActivity.runOnUiThread((Runnable) () -> mWebView.evaluateJavascript(toEvaluate, (ValueCallback<String>) null));
    }

    public void _injectApi() {
        String apiScript = _readResource(mActivity.getResources().getIdentifier("code_builder_hosted_editor", "raw", mActivity.getPackageName()));
        if (apiScript != null) {
            mWebView.evaluateJavascript(apiScript, (ValueCallback<String>) null);
        } else {
            nativeOnWebError(0, "Unable to inject api");
        }
    }

    @Nullable
    private String _readResource(int resourceId) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        InputStream stream = mActivity.getResources().openRawResource(resourceId);
        try {
            byte[] buffer = new byte[256];
            while (true) {
                int readBytes = stream.read(buffer);
                if (readBytes > 0) {
                    resource.write(buffer, 0, readBytes);
                } else {
                    stream.close();
                    return resource.toString();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read resource " + resourceId + " with error " + e.toString());
            return null;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void _createWebView() {
        if (!MainActivity.mInstance.isPublishBuild()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView = new WebView(mActivity) {
            public boolean performClick() {
                requestFocus();
                return super.performClick();
            }
        };
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mWebView.setWebViewClient(new MinecraftWebViewClient(this));
        mWebView.setWebChromeClient(new MinecraftChromeClient(this));
        mWebView.addJavascriptInterface(new WebviewHostInterface(this), "codeBuilderHostInterface");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebViewPopup = new PopupView(mActivity);
        @SuppressLint("ResourceType") View activityRootView = mActivity.findViewById(16908290).getRootView();
        mWebViewPopup.setContentView(mWebView);
        mWebViewPopup.setParentView(activityRootView);
    }
}