package com.mojang.minecraftpe;

import com.appboy.models.IInAppMessage;
import com.appboy.models.InAppMessageModal;
import com.appboy.models.MessageButton;
import com.appboy.ui.inappmessage.InAppMessageCloser;
import com.appboy.ui.inappmessage.InAppMessageOperation;
import com.appboy.ui.inappmessage.listeners.IInAppMessageManagerListener;
import java.util.List;
import java.util.Map;

public class BrazeMessageManagerListener implements IInAppMessageManagerListener {
    private MessageButton _mostRecentButton0 = null;
    private MessageButton _mostRecentButton1 = null;
    private InAppMessageModal _mostRecentInAppDialog = null;
    private IInAppMessage _mostRecentInAppToast = null;

    public native void nativeBrazeModalDialogMessageReceived(String str, String str2, String str3, String str4, String str5, String str6, String str7);
    public native void nativeBrazeToastMessageReceived(String str, String str2, String str3);

    public InAppMessageOperation beforeInAppMessageDisplayed(IInAppMessage inAppMessage) {
        if (inAppMessage.getClass().getSimpleName().equals("InAppMessageSlideup")) {
            String uri = "";
            if (inAppMessage.getUri() != null) {
                uri = inAppMessage.getUri().toString();
            }
            String subtitle = "";
            if (inAppMessage.getExtras() != null && !inAppMessage.getExtras().isEmpty()) {
                Map<String, String> extras = inAppMessage.getExtras();
                if (extras.get("ToastSubtitle") != null) {
                    subtitle = extras.get("ToastSubtitle");
                }
            }
            _mostRecentInAppToast = inAppMessage;
            nativeBrazeToastMessageReceived(inAppMessage.getMessage(), subtitle, uri);
            inAppMessage.logImpression();
        } else if (inAppMessage.getClass().getSimpleName().equals("InAppMessageModal")) {
            _mostRecentInAppDialog = (InAppMessageModal) inAppMessage;
            List<MessageButton> messageButtons = _mostRecentInAppDialog.getMessageButtons();
            _mostRecentButton0 = messageButtons.get(0);
            _mostRecentButton1 = messageButtons.get(1);
            String button0Uri = "";
            if (_mostRecentButton0.getUri() != null) {
                button0Uri = _mostRecentButton0.getUri().toString();
            }
            String button1Uri = "";
            if (_mostRecentButton1.getUri() != null) {
                button1Uri = _mostRecentButton1.getUri().toString();
            }
            nativeBrazeModalDialogMessageReceived(_mostRecentInAppDialog.getHeader(), _mostRecentInAppDialog.getMessage(), _mostRecentInAppDialog.getRemoteImageUrl(), _mostRecentButton0.getText(), button0Uri, _mostRecentButton1.getText(), button1Uri);
            inAppMessage.logImpression();
        }
        return InAppMessageOperation.DISCARD;
    }

    public void logClickOnMostRecentToast() {
        if (_mostRecentInAppToast != null) {
            _mostRecentInAppToast.logClick();
        }
    }

    public void logClickOnMostRecentDialog(int buttonNumber) {
        if (_mostRecentInAppDialog == null) {
            return;
        }
        if (buttonNumber == 0 && _mostRecentButton0 != null) {
            _mostRecentInAppDialog.logButtonClick(_mostRecentButton0);
        } else if (buttonNumber == 1 && _mostRecentButton1 != null) {
            _mostRecentInAppDialog.logButtonClick(_mostRecentButton1);
        }
    }

    public boolean onInAppMessageButtonClicked(MessageButton button, InAppMessageCloser inAppMessageCloser) {
        return true;
    }

    public boolean onInAppMessageClicked(IInAppMessage inAppMessage, InAppMessageCloser inAppMessageCloser) {
        return true;
    }

    public void onInAppMessageDismissed(IInAppMessage inAppMessage) {
    }

    public boolean onInAppMessageReceived(IInAppMessage inAppMessage) {
        return false;
    }
}
