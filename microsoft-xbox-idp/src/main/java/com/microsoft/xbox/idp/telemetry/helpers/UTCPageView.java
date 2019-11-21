package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.utc.PageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;

import java.util.ArrayList;

public class UTCPageView {
    private static final int PAGEVIEWVERSION = 1;
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
            return "Unknown";
        }
        return (String) pages.get(count - 1);
    }

    public static String getPreviousPage() {
        int count = getSize();
        if (count < 2) {
            return "Unknown";
        }
        return (String) pages.get(count - 2);
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
        track(toPage, activityTitle, new UTCAdditionalInfoModel());
    }

    public static void track(String toPage, CharSequence activityTitle, UTCAdditionalInfoModel additionalInfo) {
        if (activityTitle != null) {
            try {
                additionalInfo.addValue("activityTitle", activityTitle);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageView.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        addPage(toPage);
        String fromPage = getPreviousPage();
        PageView pageView = new PageView();
        pageView.setPageName(toPage);
        pageView.setFromPage(fromPage);
        UTCLog.log("pageView:%s, fromPage:%s, additionalInfo:%s", toPage, fromPage, additionalInfo);
        pageView.setBaseData(UTCCommonDataModel.getCommonData(1, additionalInfo));
        UTCTelemetry.LogEvent(pageView);
    }
}
