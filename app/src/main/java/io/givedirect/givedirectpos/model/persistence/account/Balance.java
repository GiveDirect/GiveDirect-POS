package io.givedirect.givedirectpos.model.persistence.account;

import android.os.Parcel;
import android.os.Parcelable;

import io.givedirect.givedirectpos.model.util.AssetUtil;

public class Balance implements Parcelable {
    private String asset_type;
    private String asset_code;
    private String asset_issuer;
    private double balance;
    private double limit;

    public Balance() {}

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }

    public String getAsset_code() {
        return AssetUtil.getAssetCode(asset_type, asset_code);
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getAsset_issuer() {
        return AssetUtil.getAssetIssuer(asset_type, asset_issuer);
    }

    public void setAsset_issuer(String asset_issuer) {
        this.asset_issuer = asset_issuer;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    protected Balance(Parcel in) {
        asset_type = in.readString();
        asset_code = in.readString();
        asset_issuer = in.readString();
        balance = in.readDouble();
        limit = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(asset_type);
        dest.writeString(asset_code);
        dest.writeString(asset_issuer);
        dest.writeDouble(balance);
        dest.writeDouble(limit);
    }

    public static final Creator<Balance> CREATOR = new Creator<Balance>() {
        @Override
        public Balance createFromParcel(Parcel in) {
            return new Balance(in);
        }

        @Override
        public Balance[] newArray(int size) {
            return new Balance[size];
        }
    };
}
