package com.mojang.minecraftpe.Webview;

import android.webkit.JavascriptInterface;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

class WebviewHostInterface {
    private MinecraftWebview mView;

    public WebviewHostInterface(MinecraftWebview view) {
        mView = view;
    }

    @JavascriptInterface
    public void sendToHost(String data) {
        System.out.println("SendToHost " + data);
        mView.nativeSendToHost(data);
    }

    @JavascriptInterface
    public void dismiss() {
        System.out.println("dismiss");
        mView.nativeDismiss();
    }
}