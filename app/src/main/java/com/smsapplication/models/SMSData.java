package com.smsapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by harshita30 on 20/10/16.
 */
public class SMSData implements Parcelable {
    public String address;
    public String date;
    public String body;

    public SMSData(String address, String date, String body) {
        this.address = address;
        this.date = date;
        this.body = body;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(date);
        dest.writeString(body);
    }
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public SMSData createFromParcel(Parcel in) {
            return new SMSData(in);
        }

        public SMSData[] newArray(int size) {
            return new SMSData[size];
        }

    };
    public SMSData(Parcel source) {
        address = source.readString();
        date = source.readString();
        body = source.readString();
    }
}
