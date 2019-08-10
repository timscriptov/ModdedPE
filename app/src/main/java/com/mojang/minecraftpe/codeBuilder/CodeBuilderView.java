package com.mojang.minecraftpe.codeBuilder;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import com.mcal.mcpelauncher.R;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.PopupView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CodeBuilderView {
    private MainActivity mActivity = MainActivity.mInstance;
    public WebView mWebView;
    public PopupView mWebViewPopup;

    public native void nativeDismiss();
    public native void nativeOnWebError(int i, String str);
    public native void nativeSendToHost(String str);

    public CodeBuilderView() {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                _createWebView();
            }
        });
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
        mActivity.runOnUiThread(new Runnable() {
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
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebView.loadUrl(urlCapture);
            }
        });
    }

    public void setShowView(boolean show) {
        final boolean showCapture = show;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebViewPopup.setVisible(showCapture);
            }
        });
    }

    public void sendToWebView(String data) {
        final String toEvaluate = String.format("window.ipcRenderer.responseFromApp('%s');", new Object[]{data.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'")});
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mWebView.evaluateJavascript(toEvaluate, null);
            }
        });
    }

    public void _injectApi() {
        String apiScript = _readResource(mActivity.getResources().getIdentifier("code_builder_hosted_editor", "raw", this.mActivity.getPackageName()));
        if (apiScript != null) {
            mWebView.evaluateJavascript(apiScript, null);
        } else {
            nativeOnWebError(0, "Unable to inject api");
        }
    }

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
        mWebView.setLayoutParams(new LayoutParams(-1, -1));
        mWebView.setWebViewClient(new CodeBuilderWebViewClient(this));
        mWebView.setWebChromeClient(new CodeBuilderChromeClient(this));
        mWebView.addJavascriptInterface(new CodeBuilderHostInterface(this), "codeBuilderHostInterface");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        mWebViewPopup = new PopupView(mActivity);
        View activityRootView = mActivity.findViewById(R.id.content).getRootView();
        mWebViewPopup.setContentView(mWebView);
        mWebViewPopup.setParentView(activityRootView);
    }
}
