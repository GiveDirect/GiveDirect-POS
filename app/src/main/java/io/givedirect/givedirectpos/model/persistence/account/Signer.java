package io.givedirect.givedirectpos.model.persistence.account;

import android.os.Parcel;
import android.os.Parcelable;

public class Signer implements Parcelable {
    private String public_key;
    private int weight;
    private String key;
    private String type;

    public Signer() {}

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    protected Signer(Parcel in) {
        public_key = in.readString();
        weight = in.readInt();
        key = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(public_key);
        dest.writeInt(weight);
        dest.writeString(key);
        dest.writeString(type);
    }

    public static final Creator<Signer> CREATOR = new Creator<Signer>() {
        @Override
        public Signer createFromParcel(Parcel in) {
            return new Signer(in);
        }

        @Override
        public Signer[] newArray(int size) {
            return new Signer[size];
        }
    };
}
