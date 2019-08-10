package com.mojang.minecraftpe.codeBuilder;

import android.webkit.JavascriptInterface;

class CodeBuilderHostInterface {
    private CodeBuilderView mView;
    public CodeBuilderHostInterface(CodeBuilderView view) {
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
