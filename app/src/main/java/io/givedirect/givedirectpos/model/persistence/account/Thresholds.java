package io.givedirect.givedirectpos.model.persistence.account;

import android.os.Parcel;
import android.os.Parcelable;

public class Thresholds implements Parcelable {
    private int low_threshold;
    private int med_threshold;
    private int high_threshold;

    public Thresholds() {}

    public int getLow_threshold() {
        return low_threshold;
    }

    public void setLow_threshold(int low_threshold) {
        this.low_threshold = low_threshold;
    }

    public int getMed_threshold() {
        return med_threshold;
    }

    public void setMed_threshold(int med_threshold) {
        this.med_threshold = med_threshold;
    }

    public int getHigh_threshold() {
        return high_threshold;
    }

    public void setHigh_threshold(int high_threshold) {
        this.high_threshold = high_threshold;
    }

    protected Thresholds(Parcel in) {
        low_threshold = in.readInt();
        med_threshold = in.readInt();
        high_threshold = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(low_threshold);
        dest.writeInt(med_threshold);
        dest.writeInt(high_threshold);
    }

    public static final Creator<Thresholds> CREATOR = new Creator<Thresholds>() {
        @Override
        public Thresholds createFromParcel(Parcel in) {
            return new Thresholds(in);
        }

        @Override
        public Thresholds[] newArray(int size) {
            return new Thresholds[size];
        }
    };
}
