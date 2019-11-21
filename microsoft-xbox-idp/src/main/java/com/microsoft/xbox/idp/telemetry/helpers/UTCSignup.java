package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult;

public class UTCSignup {
    public static void trackSearchGamerTag(AccountProvisioningResult result, CharSequence activityTitle) {
        if (result != null) {
            try {
                UTCCommonDataModel.setUserId(result.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Signup - Search for gamertag", activityTitle);
    }

    public static void trackSignInWithDifferentUser(AccountProvisioningResult result, CharSequence activityTitle) {
        if (result != null) {
            try {
                UTCCommonDataModel.setUserId(result.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Signup - Signin with different user", activityTitle);
    }

    public static void trackClaimGamerTag(AccountProvisioningResult result, CharSequence activityTitle) {
        if (result != null) {
            try {
                UTCCommonDataModel.setUserId(result.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Signup - Claim gamertag", activityTitle);
    }

    public static void trackClearGamerTag(AccountProvisioningResult result, CharSequence activityTitle) {
        if (result != null) {
            try {
                UTCCommonDataModel.setUserId(result.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track("Signup - Clear gamertag text", activityTitle);
    }

    public static void trackPageView(CharSequence activityTitle) {
        try {
            UTCPageView.track("Sign up view", activityTitle);
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
