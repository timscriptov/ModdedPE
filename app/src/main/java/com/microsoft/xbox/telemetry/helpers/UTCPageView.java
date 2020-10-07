package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.utc.PageView;

import java.util.ArrayList;
import java.util.HashMap;

public class UTCPageView {
    private static ArrayList<String> pages = new ArrayList<>();

    public static int getSize() {
        if (pages == null) {
            pages = new ArrayList<>();
        }
        return pages.size();
    }

    public static String getCurrentPage() {
        int count = getSize();
        if (count == 0) {
            return UTCTelemetry.UNKNOWNPAGE;
        }
        return pages.get(count - 1);
    }

    public static String getPreviousPage() {
        int count = getSize();
        if (count < 2) {
            return UTCTelemetry.UNKNOWNPAGE;
        }
        return pages.get(count - 2);
    }

    public static void addPage(String newPage) {
        if (pages == null) {
            pages = new ArrayList<>();
        }
        if (!pages.contains(newPage) && newPage != null) {
            pages.add(newPage);
        }
    }

    public static void removePage() {
        int count = getSize();
        if (count > 0) {
            pages.remove(count - 1);
        }
    }

    public static void track(String toPage, CharSequence activityTitle) {
        track(toPage, activityTitle, new HashMap());
    }

    public static void track(String toPage, CharSequence activityTitle, HashMap<String, Object> additionalInfo) {
        if (activityTitle != null) {
            try {
                additionalInfo.put("activityTitle", activityTitle);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageView.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        addPage(toPage);
        String fromPage = getPreviousPage();
        PageView pageView = new PageView();
        pageView.pageName = toPage;
        pageView.fromPage = fromPage;
        pageView.additionalInfo = additionalInfo;
        UTCLog.log("pageView:%s, fromPage:%s, additionalInfo:%s", toPage, fromPage, additionalInfo);
        UTCTelemetry.LogEvent(pageView);
    }
}
