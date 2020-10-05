package com.microsoft.xbox.idp.util;

import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FragmentLoaderKey implements Parcelable {
    public static final Parcelable.Creator<FragmentLoaderKey> CREATOR = new Parcelable.Creator<FragmentLoaderKey>() {
        @NotNull
        @Contract("_ -> new")
        public FragmentLoaderKey createFromParcel(Parcel in) {
            return new FragmentLoaderKey(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public FragmentLoaderKey[] newArray(int size) {
            return new FragmentLoaderKey[size];
        }
    };
    static final boolean assertionsDisabled;

    static {
        boolean z;
        if (!FragmentLoaderKey.class.desiredAssertionStatus()) {
            z = true;
        } else {
            z = false;
        }
        assertionsDisabled = z;
    }

    private final String className;
    private final int loaderId;

    public FragmentLoaderKey(Class<? extends Fragment> cls, int loaderId2) {
        if (assertionsDisabled || cls != null) {
            className = cls.getName();
            loaderId = loaderId2;
            return;
        }
        throw new AssertionError();
    }

    protected FragmentLoaderKey(@NotNull Parcel in) {
        className = in.readString();
        loaderId = in.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FragmentLoaderKey that = (FragmentLoaderKey) obj;
        if (loaderId == that.loaderId) {
            return className.equals(that.className);
        }
        return false;
    }

    public int hashCode() {
        return (className.hashCode() * 31) + loaderId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeString(className);
        dest.writeInt(loaderId);
    }
}
