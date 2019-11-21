package com.mojang.minecraftpe.Webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.mojang.minecraftpe.MainActivity;

class MinecraftChromeClient extends WebChromeClient {
    private MinecraftWebview mView;

    public MinecraftChromeClient(MinecraftWebview view) {
        this.mView = view;
    }

    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        MainActivity.mInstance.runOnUiThread(new Runnable() {
            public void run() {
                mView._injectApi();
            }
        });
    }
}
