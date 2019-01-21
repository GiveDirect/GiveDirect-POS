package io.givedirect.givedirectpos.model.persistence.account;

import android.os.Parcel;
import android.os.Parcelable;

public class Flags implements Parcelable {
    private boolean auth_required;
    private boolean auth_revocable;

    public Flags() {}

    public boolean isAuth_required() {
        return auth_required;
    }

    public void setAuth_required(boolean auth_required) {
        this.auth_required = auth_required;
    }

    public boolean isAuth_revocable() {
        return auth_revocable;
    }

    public void setAuth_revocable(boolean auth_revocable) {
        this.auth_revocable = auth_revocable;
    }

    protected Flags(Parcel in) {
        auth_required = in.readByte() != 0;
        auth_revocable = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (auth_required ? 1 : 0));
        dest.writeByte((byte) (auth_revocable ? 1 : 0));
    }

    public static final Creator<Flags> CREATOR = new Creator<Flags>() {
        @Override
        public Flags createFromParcel(Parcel in) {
            return new Flags(in);
        }

        @Override
        public Flags[] newArray(int size) {
            return new Flags[size];
        }
    };
}
