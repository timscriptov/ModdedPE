package com.microsoft.xbox.idp.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.microsoft.xbox.idp.R;

public class AccountProvisioningResult implements Parcelable {
    public static final Creator<AccountProvisioningResult> CREATOR = new Creator<AccountProvisioningResult>() {
        public AccountProvisioningResult createFromParcel(Parcel in) {
            return new AccountProvisioningResult(in);
        }

        public AccountProvisioningResult[] newArray(int size) {
            return new AccountProvisioningResult[size];
        }
    };
    public static final String TAG = AccountProvisioningResult.class.getSimpleName();
    private AgeGroup ageGroup;
    private final String gamerTag;
    private final String xuid;

    public enum AgeGroup {
        Adult(R.string.xbid_age_group_adult, R.string.xbid_age_group_adult_details_android),
        Teen(R.string.xbid_age_group_teen, R.string.xbid_age_group_teen_details_android),
        Child(R.string.xbid_age_group_child, R.string.xbid_age_group_child_details_android);

        public final int resIdAgeGroup;
        public final int resIdAgeGroupDetails;

        private AgeGroup(int resIdAgeGroup2, int resIdAgeGroupDetails2) {
            this.resIdAgeGroup = resIdAgeGroup2;
            this.resIdAgeGroupDetails = resIdAgeGroupDetails2;
        }

        public static AgeGroup fromServiceString(String serviceString) {
            Log.d(AccountProvisioningResult.TAG, "Creating AgeGroup from '" + serviceString + "'");
            if (!TextUtils.isEmpty(serviceString)) {
                if ("adult".compareToIgnoreCase(serviceString) == 0) {
                    return Adult;
                }
                if ("teen".compareToIgnoreCase(serviceString) == 0) {
                    return Teen;
                }
                if ("child".compareToIgnoreCase(serviceString) == 0) {
                    return Child;
                }
            }
            return null;
        }
    }

    public AccountProvisioningResult(String gamerTag2, String xuid2) {
        this.gamerTag = gamerTag2;
        this.xuid = xuid2;
    }

    protected AccountProvisioningResult(Parcel in) {
        this.gamerTag = in.readString();
        this.xuid = in.readString();
        int ordinal = in.readInt();
        this.ageGroup = ordinal != -1 ? AgeGroup.values()[ordinal] : null;
    }

    public String getGamerTag() {
        return this.gamerTag;
    }

    public String getXuid() {
        return this.xuid;
    }

    public AgeGroup getAgeGroup() {
        return this.ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup2) {
        this.ageGroup = ageGroup2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gamerTag);
        dest.writeString(this.xuid);
        dest.writeInt(this.ageGroup != null ? this.ageGroup.ordinal() : -1);
    }
}
