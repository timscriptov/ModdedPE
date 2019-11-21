package com.microsoft.xbox.idp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Suggestions {

    public static class Request {
        public int Algorithm;
        public int Count;
        public String Locale;
        public String Seed;
    }

    public static class Response implements Parcelable {
        public static final Creator<Response> CREATOR = new Creator<Response>() {
            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
        public ArrayList<String> Gamertags;

        protected Response(Parcel in) {
            this.Gamertags = in.createStringArrayList();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(this.Gamertags);
        }
    }
}
