package com.mojang.minecraftpe.Webview;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.PopupView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MinecraftWebview {
    private MainActivity mActivity = MainActivity.mInstance;
    private WebView mWebView;
    private PopupView mWebViewPopup;

    public native void nativeDismiss();
    public native void nativeOnWebError(int i, String str);
    public native void nativeSendToHost(String str);

    public MinecraftWebview() {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                _createWebView();
            }
        });
    }

    public void teardown() {
        this.mWebViewPopup.dismiss();
        this.mWebViewPopup = null;
        this.mWebView = null;
        this.mActivity = null;
    }

    public void setRect(float minX, float maxX, float minY, float maxY) {
        final int x0 = (int) minX;
        final int x1 = (int) maxX;
        final int y0 = (int) minY;
        final int y1 = (int) maxY;
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebViewPopup.setRect(x0, x1, y0, y1);
                mWebViewPopup.update();
            }
        });
    }

    public void setPropagatedAlpha(float alpha) {
        setShowView(((double) alpha) == 1.0d);
    }

    public void setUrl(String url) {
        final String urlCapture = url;
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebView.loadUrl(urlCapture);
            }
        });
    }

    public void setShowView(boolean show) {
        final boolean showCapture = show;
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebViewPopup.setVisible(showCapture);
            }
        });
    }

    public void sendToWebView(String data) {
        final String toEvaluate = data;
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebView.evaluateJavascript(toEvaluate, null);
            }
        });
    }

    public void _injectApi() {
        String apiScript = _readResource(this.mActivity.getResources().getIdentifier("code_builder_hosted_editor", "raw", this.mActivity.getPackageName()));
        if (apiScript != null) {
            this.mWebView.evaluateJavascript(apiScript, null);
        } else {
            nativeOnWebError(0, "Unable to inject api");
        }
    }

    private String _readResource(int resourceId) {
        ByteArrayOutputStream resource = new ByteArrayOutputStream();
        InputStream stream = this.mActivity.getResources().openRawResource(resourceId);
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

    private void _createWebView() {
        if (!MainActivity.mInstance.isPublishBuild()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.mWebView = new WebView(this.mActivity) {
            public boolean performClick() {
                requestFocus();
                return super.performClick();
            }
        };
        this.mWebView.setLayoutParams(new LayoutParams(-1, -1));
        this.mWebView.setWebViewClient(new MinecraftWebViewClient(this));
        this.mWebView.setWebChromeClient(new MinecraftChromeClient(this));
        this.mWebView.addJavascriptInterface(new WebviewHostInterface(this), "codeBuilderHostInterface");
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        this.mWebViewPopup = new PopupView(this.mActivity);
        View activityRootView = this.mActivity.findViewById(16908290).getRootView();
        this.mWebViewPopup.setContentView(this.mWebView);
        this.mWebViewPopup.setParentView(activityRootView);
    }
}
