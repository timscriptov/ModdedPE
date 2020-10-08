package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.utc.PageAction;

import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UTCPageAction {
    public static void track(String actionName, CharSequence activityTitle) {
        track(actionName, UTCPageView.getCurrentPage(), activityTitle, new HashMap());
    }

    public static void track(String actionName, CharSequence activityTitle, HashMap<String, Object> additionalInfo) {
        track(actionName, UTCPageView.getCurrentPage(), activityTitle, additionalInfo);
    }

    public static void track(String actionName, String onPageName, CharSequence activityTitle, HashMap<String, Object> additionalInfo) {
        if (activityTitle != null) {
            try {
                additionalInfo.put("activityTitle", activityTitle);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageAction.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        PageAction pageAction = new PageAction();
        pageAction.actionName = actionName;
        pageAction.pageName = onPageName;
        pageAction.additionalInfo = additionalInfo;
        UTCLog.log("pageActions:%s, onPage:%s, additionalInfo:%s", actionName, onPageName, additionalInfo);
        UTCTelemetry.LogEvent(pageAction);
    }
}
