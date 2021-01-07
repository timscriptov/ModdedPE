package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.UTCDateConverterGson;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.XLEConstants;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/**
 * 07.01.2021
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

        public BroadcastRecord getBroadcastRecord(long j) {
            if (!this.broadcastRecordSet) {
                if ("Online".equalsIgnoreCase(this.state)) {
                    Iterator<DeviceRecord> it = this.devices.iterator();
                    loop0:
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DeviceRecord next = it.next();
                        if (next.isXboxOne()) {
                            Iterator<TitleRecord> it2 = next.titles.iterator();
                            while (it2.hasNext()) {
                                TitleRecord next2 = it2.next();
                                if (next2.id == j && next2.isRunningInFullOrFill() && next2.activity != null && next2.activity.broadcast != null) {
                                    this.broadcastRecord = next2.activity.broadcast;
                                    break loop0;
                                }
                            }
                            continue;
                        }
                    }
                }
                this.broadcastRecordSet = true;
            }
            return this.broadcastRecord;
        }

        public int getBroadcastingViewerCount(long j) {
            BroadcastRecord broadcastRecord2 = getBroadcastRecord(j);
            if (broadcastRecord2 == null) {
                return 0;
            }
            return broadcastRecord2.viewers;
        }

        public long getXboxOneNowPlayingTitleId() {
            long j = -1;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord next = it.next();
                    if (next.isXboxOne()) {
                        Iterator<TitleRecord> it2 = next.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord next2 = it2.next();
                            if (next2.isRunningInFullOrFill()) {
                                j = next2.id;
                                break;
                            }
                        }
                    }
                }
            }
            return j;
        }

        public Date getXboxOneNowPlayingDate() {
            Date date = null;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord next = it.next();
                    if (next.isXboxOne()) {
                        Iterator<TitleRecord> it2 = next.titles.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TitleRecord next2 = it2.next();
                            if (next2.isRunningInFullOrFill()) {
                                date = next2.lastModified;
                                break;
                            }
                        }
                    }
                }
            }
            return date;
        }
    }

    public static class DeviceRecord {
        public ArrayList<TitleRecord> titles;
        public String type;

        public boolean isXboxOne() {
            return "XboxOne".equalsIgnoreCase(this.type);
        }

        public boolean isXbox360() {
            return "Xbox360".equalsIgnoreCase(this.type);
        }
    }

    public static class TitleRecord {
        public ActivityRecord activity;
        public long id;
        public Date lastModified;
        public String name;
        public String placement;

        public boolean isRunningInFullOrFill() {
            return "Full".equalsIgnoreCase(this.placement) || "Fill".equalsIgnoreCase(this.placement);
        }

        public boolean isDash() {
            return this.id == XLEConstants.DASH_TITLE_ID;
        }
    }

    public static class FollowersPresenceResult {
        public ArrayList<UserPresence> userPresence;

        public static @Nullable FollowersPresenceResult deserialize(InputStream inputStream) {
            UserPresence[] userPresenceArr = (UserPresence[]) GsonUtil.deserializeJson(inputStream, UserPresence[].class, (Type) Date.class, (Object) new UTCDateConverterGson.UTCDateConverterJSONDeserializer());
            if (userPresenceArr == null) {
                return null;
            }
            FollowersPresenceResult followersPresenceResult = new FollowersPresenceResult();
            followersPresenceResult.userPresence = new ArrayList<>(Arrays.asList(userPresenceArr));
            return followersPresenceResult;
        }
    }
}
