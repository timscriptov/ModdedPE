package com.mojang.minecraftpe;

import com.appboy.models.IInAppMessage;
import com.appboy.models.InAppMessageModal;
import com.appboy.models.MessageButton;
import com.appboy.ui.inappmessage.InAppMessageCloser;
import com.appboy.ui.inappmessage.InAppMessageOperation;
import com.appboy.ui.inappmessage.listeners.IInAppMessageManagerListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BrazeMessageManagerListener implements IInAppMessageManagerListener {
    private MessageButton _mostRecentButton0 = null;
    private MessageButton _mostRecentButton1 = null;
    private InAppMessageModal _mostRecentInAppDialog = null;
    private IInAppMessage _mostRecentInAppToast = null;

    public native void nativeBrazeModalDialogMessageReceived(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    public native void nativeBrazeToastMessageReceived(String str, String str2, String str3);

    public boolean onInAppMessageButtonClicked(MessageButton messageButton, InAppMessageCloser inAppMessageCloser) {
        return true;
    }

    public boolean onInAppMessageClicked(IInAppMessage iInAppMessage, InAppMessageCloser inAppMessageCloser) {
        return true;
    }

    public void onInAppMessageDismissed(IInAppMessage iInAppMessage) {
    }

    public boolean onInAppMessageReceived(IInAppMessage iInAppMessage) {
        return false;
    }

    public InAppMessageOperation beforeInAppMessageDisplayed(@NotNull IInAppMessage iInAppMessage) {
        String str = "";
        if (iInAppMessage.getClass().getSimpleName().equals("InAppMessageSlideup")) {
            String uri = iInAppMessage.getUri() != null ? iInAppMessage.getUri().toString() : str;
            if (iInAppMessage.getExtras() != null && !iInAppMessage.getExtras().isEmpty()) {
                Map<String, String> extras = iInAppMessage.getExtras();
                if (extras.get("ToastSubtitle") != null) {
                    str = extras.get("ToastSubtitle");
                }
            }
            this._mostRecentInAppToast = iInAppMessage;
            nativeBrazeToastMessageReceived(iInAppMessage.getMessage(), str, uri);
            iInAppMessage.logImpression();
        } else if (iInAppMessage.getClass().getSimpleName().equals("InAppMessageModal")) {
            InAppMessageModal inAppMessageModal = (InAppMessageModal) iInAppMessage;
            _mostRecentInAppDialog = inAppMessageModal;
            List<MessageButton> messageButtons = inAppMessageModal.getMessageButtons();
            _mostRecentButton0 = messageButtons.get(0);
            _mostRecentButton1 = messageButtons.get(1);
            String uri2 = _mostRecentButton0.getUri() != null ? _mostRecentButton0.getUri().toString() : str;
            if (_mostRecentButton1.getUri() != null) {
                str = _mostRecentButton1.getUri().toString();
            }
            nativeBrazeModalDialogMessageReceived(_mostRecentInAppDialog.getHeader(), _mostRecentInAppDialog.getMessage(), _mostRecentInAppDialog.getRemoteImageUrl(), _mostRecentButton0.getText(), uri2, _mostRecentButton1.getText(), str);
            iInAppMessage.logImpression();
        }
        return InAppMessageOperation.DISCARD;
    }

    public void logClickOnMostRecentToast() {
        IInAppMessage iInAppMessage = _mostRecentInAppToast;
        if (iInAppMessage != null) {
            iInAppMessage.logClick();
        }
    }

    public void logClickOnMostRecentDialog(int i) {
        MessageButton messageButton;
        MessageButton messageButton2;
        InAppMessageModal inAppMessageModal = _mostRecentInAppDialog;
        if (inAppMessageModal == null) {
            return;
        }
        if (i == 0 && (messageButton2 = _mostRecentButton0) != null) {
            inAppMessageModal.logButtonClick(messageButton2);
        } else if (i == 1 && (messageButton = _mostRecentButton1) != null) {
            _mostRecentInAppDialog.logButtonClick(messageButton);
        }
    }
}