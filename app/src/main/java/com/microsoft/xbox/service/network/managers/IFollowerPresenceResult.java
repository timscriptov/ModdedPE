package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.UTCDateConverterGson;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.XLEConstants;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface IFollowerPresenceResult {

    public static class ActivityRecord {
        public BroadcastRecord broadcast;
        public String richPresence;
    }

    public static class BroadcastRecord {
        public String id;
        public String provider;
        public String session;
        public int viewers;
    }

    public static class LastSeenRecord {
        public String deviceType;
        public String titleName;
    }

    public static class UserPresence {
        public ArrayList<DeviceRecord> devices;
        public LastSeenRecord lastSeen;
        public String state;
        public String xuid;
        private BroadcastRecord broadcastRecord;
        private boolean broadcastRecordSet;

        public BroadcastRecord getBroadcastRecord(long titleId) {
            if (!broadcastRecordSet) {
                if ("Online".equalsIgnoreCase(state)) {
                    Iterator<DeviceRecord> it = devices.iterator();
                    loop0:
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DeviceRecord device = it.next();
                        if (device.isXboxOne()) {
                            Iterator<TitleRecord> it2 = device.titles.iterator();
                            while (it2.hasNext()) {
                                TitleRecord title = it2.next();
                                if (title.id == titleId && title.isRunningInFullOrFill() && title.activity != null && title.activity.broadcast != null) {
                                    broadcastRecord = title.activity.broadcast;
                                    break loop0;
                                }
                            }
                            continue;
                        }
                    }
                }
                broadcastRecordSet = true;
            }
            return broadcastRecord;
        }

        public int getBroadcastingViewerCount(long titleId) {
            BroadcastRecord r = getBroadcastRecord(titleId);
            if (r == null) {
                return 0;
            }
            return r.viewers;
        }

        public long getXboxOneNowPlayingTitleId() {
            long result = -1;
            if ("Online".equalsIgnoreCase(state)) {
                Iterator<DeviceRecord> it = devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord device = it.next();
                    if (device.isXboxOne()) {
                        Iterator<TitleRecord> it2 = device.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord title = it2.next();
                            if (title.isRunningInFullOrFill()) {
                                result = title.id;
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }

        public Date getXboxOneNowPlayingDate() {
            Date result = null;
            if ("Online".equalsIgnoreCase(state)) {
                Iterator<DeviceRecord> it = devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord device = it.next();
                    if (device.isXboxOne()) {
                        Iterator<TitleRecord> it2 = device.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord title = it2.next();
                            if (title.isRunningInFullOrFill()) {
                                result = title.lastModified;
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    public static class DeviceRecord {
        public ArrayList<TitleRecord> titles;
        public String type;

        public boolean isXboxOne() {
            return "XboxOne".equalsIgnoreCase(type);
        }

        public boolean isXbox360() {
            return "Xbox360".equalsIgnoreCase(type);
        }
    }

    public static class TitleRecord {
        public ActivityRecord activity;
        public long id;
        public Date lastModified;
        public String name;
        public String placement;

        public boolean isRunningInFullOrFill() {
            return "Full".equalsIgnoreCase(placement) || "Fill".equalsIgnoreCase(placement);
        }

        public boolean isDash() {
            return id == XLEConstants.DASH_TITLE_ID;
        }
    }

    public static class FollowersPresenceResult {
        public ArrayList<UserPresence> userPresence;

        @Nullable
        public static FollowersPresenceResult deserialize(InputStream stream) {
            UserPresence[] data = GsonUtil.deserializeJson(stream, UserPresence[].class, Date.class, new UTCDateConverterGson.UTCDateConverterJSONDeserializer());
            if (data == null) {
                return null;
            }
            FollowersPresenceResult result = new FollowersPresenceResult();
            result.userPresence = new ArrayList<>(Arrays.asList(data));
            return result;
        }
    }
}