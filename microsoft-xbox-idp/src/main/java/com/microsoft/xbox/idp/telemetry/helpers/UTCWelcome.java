package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;

public class UTCWelcome {
    public static void trackDone(User user, CharSequence activityTitle) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Welcome - Done", activityTitle);
    }

    public static void trackChangeUser(User user, CharSequence activityTitle) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Welcome - Change user", activityTitle);
    }

    public static void trackPageView(User user, CharSequence activityTitle) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageView.track("Welcome view", activityTitle);
    }
}
