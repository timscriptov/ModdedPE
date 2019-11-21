package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.utc.PageAction;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;

public class UTCPageAction {
    private static final int PAGEACTIONVERSION = 1;

    public static void track(String actionName, CharSequence activityTitle) {
        track(actionName, UTCPageView.getCurrentPage(), activityTitle, new UTCAdditionalInfoModel());
    }

    public static void track(String actionName, CharSequence activityTitle, UTCAdditionalInfoModel model) {
        track(actionName, UTCPageView.getCurrentPage(), activityTitle, model);
    }

    public static void track(String actionName, String onPageName, CharSequence activityTitle, UTCAdditionalInfoModel additionalInfo) {
        if (activityTitle != null) {
            try {
                additionalInfo.addValue("activityTitle", activityTitle);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageAction.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        PageAction pageAction = new PageAction();
        pageAction.setActionName(actionName);
        pageAction.setPageName(onPageName);
        pageAction.setBaseData(UTCCommonDataModel.getCommonData(1, additionalInfo));
        UTCLog.log("pageActions:%s, onPage:%s, additionalInfo:%s", actionName, onPageName, additionalInfo);
        UTCTelemetry.LogEvent(pageAction);
    }
}
