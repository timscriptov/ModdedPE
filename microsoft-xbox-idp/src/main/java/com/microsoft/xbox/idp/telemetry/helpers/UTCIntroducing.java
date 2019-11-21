package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;

public class UTCIntroducing {
    public static void trackDone(User user, CharSequence activityTitle) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCIntroducing.trackDone");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Introducing - Done", activityTitle);
    }

    public static void trackPageView(User user, CharSequence activityTitle) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCIntroducing.trackPageView");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageView.track("Introducing view", activityTitle);
    }
}
