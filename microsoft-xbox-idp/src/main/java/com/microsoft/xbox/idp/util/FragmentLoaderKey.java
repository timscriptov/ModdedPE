package com.microsoft.xbox.idp.util;

import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;

public class FragmentLoaderKey implements Parcelable {
    static final boolean $assertionsDisabled;
    public static final Creator<FragmentLoaderKey> CREATOR = new Creator<FragmentLoaderKey>() {
        public FragmentLoaderKey createFromParcel(Parcel in) {
            return new FragmentLoaderKey(in);
        }

        public FragmentLoaderKey[] newArray(int size) {
            return new FragmentLoaderKey[size];
        }
    };
    private final String className;
    private final int loaderId;

    static {
        boolean z;
        if (!FragmentLoaderKey.class.desiredAssertionStatus()) {
            z = true;
        } else {
            z = false;
        }
        $assertionsDisabled = z;
    }

    public FragmentLoaderKey(Class<? extends Fragment> cls, int loaderId2) {
        if ($assertionsDisabled || cls != null) {
            this.className = cls.getName();
            this.loaderId = loaderId2;
            return;
        }
        throw new AssertionError();
    }

    protected FragmentLoaderKey(Parcel in) {
        this.className = in.readString();
        this.loaderId = in.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FragmentLoaderKey that = (FragmentLoaderKey) obj;
        if (this.loaderId == that.loaderId) {
            return this.className.equals(that.className);
        }
        return false;
    }

    public int hashCode() {
        return (this.className.hashCode() * 31) + this.loaderId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.className);
        dest.writeInt(this.loaderId);
    }
}
