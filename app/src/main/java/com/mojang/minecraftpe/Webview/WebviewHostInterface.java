package com.mojang.minecraftpe.Webview;

import android.webkit.JavascriptInterface;

class WebviewHostInterface {
    private MinecraftWebview mView;

    public WebviewHostInterface(MinecraftWebview view) {
        this.mView = view;
    }

    @JavascriptInterface
    public void sendToHost(String data) {
        System.out.println("SendToHost " + data);
        this.mView.nativeSendToHost(data);
    }

    @JavascriptInterface
    public void dismiss() {
        System.out.println("dismiss");
        this.mView.nativeDismiss();
    }
}
