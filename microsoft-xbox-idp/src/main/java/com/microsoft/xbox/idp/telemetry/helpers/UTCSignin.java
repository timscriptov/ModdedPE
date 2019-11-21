package com.microsoft.xbox.idp.telemetry.helpers;

import com.facebook.share.internal.ShareConstants;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.MSA;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.XboxAccount;

public class UTCSignin {
    private static CharSequence activityTitle = null;

    public static CharSequence getCurrentActivity() {
        return activityTitle;
    }

    public static void setCurrentActivity(CharSequence activityTitle2) {
        activityTitle = activityTitle2;
    }

    public static void trackXBLSigninStart(String cid, CharSequence activityTitle2) {
        try {
            setCurrentActivity(activityTitle2);
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            UTCPageAction.track(XboxAccount.Start, activityTitle2, model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackXBLSigninStart");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackXBLSigninSuccess(String cid, CharSequence activityTitle2, boolean createAccount) {
        try {
            setCurrentActivity(activityTitle2);
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("createdXBLAccount", Boolean.valueOf(createAccount));
            UTCPageAction.track(XboxAccount.Success, activityTitle2, model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackXBLSigninSuccess");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSASigninStart(String cid, boolean isSilent, CharSequence activityTitle2) {
        try {
            setCurrentActivity(activityTitle2);
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            UTCPageAction.track(MSA.Start, activityTitle2, model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackMSASigninStart");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSASigninSuccess(String cid, boolean isSilent, CharSequence activityTitle2) {
        try {
            setCurrentActivity(activityTitle2);
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            UTCPageAction.track(MSA.Success, activityTitle2, model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackMSASigninFinish");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSignin(String cid, boolean isSilent, CharSequence activityTitle2) {
        try {
            setCurrentActivity(activityTitle2);
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            UTCPageAction.track("Signin - Sign in", activityTitle2, model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackSignin");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackAccountAcquired(String job, String cid, boolean isSilent) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("job", job);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, CallBackSources.Account);
            UTCPageAction.track("Signin - Account acquired", getCurrentActivity(), model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackAccountAcquired");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackTicketAcquired(String job, String cid, boolean isSilent) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("cid", cid);
            model.addValue("job", job);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, CallBackSources.Ticket);
            UTCPageAction.track("Signin - Ticket acquired", getCurrentActivity(), model);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackTicketAcquired");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackPageView(CharSequence activityTitle2) {
        try {
            UTCPageView.track("Sign in view", activityTitle2);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackPageView");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
