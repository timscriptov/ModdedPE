package com.mojang.minecraftpe.codeBuilder;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.mojang.minecraftpe.MainActivity;

class CodeBuilderWebViewClient extends WebViewClient {
    private CodeBuilderView mView;
    public CodeBuilderWebViewClient(CodeBuilderView view) {
        mView = view;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        System.out.println("Started loading " + url);
        super.onPageStarted(view, url, favicon);
    }

    public void onPageFinished(WebView view, String url) {
        System.out.println("Finished loading " + url);
        super.onPageFinished(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        System.out.println(String.format("Error %s loading url %s", new Object[]{error.getDescription().toString(), request.getUrl().toString()}));
        mView.nativeOnWebError(error.getErrorCode(), error.getDescription().toString());
        super.onReceivedError(view, request, error);
    }

    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri newUrl = request.getUrl();
        Uri oldUrl = Uri.parse(view.getUrl());
        if (!request.hasGesture() || oldUrl.getHost().equals(newUrl.getHost())) {
            return super.shouldOverrideUrlLoading(view, request);
        }
        MainActivity.mInstance.launchUri(request.getUrl().toString());
        return true;
    }
}