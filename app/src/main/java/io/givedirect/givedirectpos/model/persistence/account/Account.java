package io.givedirect.givedirectpos.model.persistence.account;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.TransactionBuilderAccount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.givedirect.givedirectpos.GiveDirectPOSApp;
import io.givedirect.givedirectpos.model.util.AssetUtil;
import io.givedirect.givedirectpos.view.util.TextUtils;
import io.reactivex.Observable;

@TypeConverters(Account.AccountTypeConverters.class)
public class Account implements Parcelable, TransactionBuilderAccount {
    public static final String INVALID_ID = "INVALID_ID";

    private String id;
    private String paging_token;
    private String account_id;
    private long sequence;
    private long subentry_count;
    private Thresholds thresholds;
    private Flags flags;
    private List<Balance> balances;
    private List<Signer> signers;
    private Map<String, String> data;
    private String inflation_destination;

    public Account() {
        id = INVALID_ID;
    }

    public boolean isOnNetwork() {
        return true;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPaging_token() {
        return paging_token;
    }

    public void setPaging_token(String paging_token) {
        this.paging_token = paging_token;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getSubentry_count() {
        return subentry_count;
    }

    public void setSubentry_count(long subentry_count) {
        this.subentry_count = subentry_count;
    }

    public Thresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(Thresholds thresholds) {
        this.thresholds = thresholds;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Balance getLumens() {
        return Observable.fromIterable(balances)
                .filter(AssetUtil::isLumenBalance)
                .firstElement()
                .defaultIfEmpty(new Balance())
                .blockingGet();
    }

    public String getInflation_destination() {
        return inflation_destination;
    }

    public void setInflation_destination(String inflation_destination) {
        this.inflation_destination = inflation_destination;
    }

    protected Account(Parcel in) {
        id = in.readString();
        paging_token = in.readString();
        account_id = in.readString();
        sequence = in.readLong();
        subentry_count = in.readLong();
        thresholds = in.readParcelable(Thresholds.class.getClassLoader());
        flags = in.readParcelable(Flags.class.getClassLoader());

        this.balances = new ArrayList<>();
        in.readTypedList(balances, Balance.CREATOR);

        this.signers = new ArrayList<>();
        in.readTypedList(signers, Signer.CREATOR);

        int dataSize = in.readInt();
        if (dataSize > 0) {
            data = new HashMap<>(dataSize);
            for (int i = 0; i < dataSize; i++) {
                String key = in.readString();
                String value = in.readString();
                data.put(key, value);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(paging_token);
        dest.writeString(account_id);
        dest.writeLong(sequence);
        dest.writeLong(subentry_count);
        dest.writeParcelable(thresholds, flags);
        dest.writeParcelable(this.flags, flags);
        dest.writeTypedList(balances);
        dest.writeTypedList(signers);

        int dataSize = data == null ? 0 : data.size();
        dest.writeInt(dataSize);
        if (dataSize > 0) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public KeyPair getKeypair() {
        return KeyPair.fromAccountId(account_id);
    }

    @Override
    public Long getSequenceNumber() {
        return sequence;
    }

    @Override
    public Long getIncrementedSequenceNumber() {
        return sequence + 1;
    }

    @Override
    public void incrementSequenceNumber() {
        sequence++;
    }

    public static class AccountTypeConverters {
        private static final JsonAdapter<List<Balance>> balanceAdapter;
        private static final JsonAdapter<List<Signer>> signerAdapter;
        private static final JsonAdapter<Map<String, String>> dataAdapter;

        static {
            Moshi moshi = GiveDirectPOSApp.getInstance().getMoshi();
            balanceAdapter = moshi.adapter(Types.newParameterizedType(List.class, Balance.class));
            signerAdapter = moshi.adapter(Types.newParameterizedType(List.class, Signer.class));
            dataAdapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
        }

        @TypeConverter
        public static List<Balance> stringToBalance(String json) {
            if (TextUtils.isEmpty(json)) {
                return new ArrayList<>();
            }

            try {
                return balanceAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for balance", e);
            }
        }

        @TypeConverter
        public static String balanceToString(List<Balance> list) {
            return balanceAdapter.toJson(list);
        }

        @TypeConverter
        public static List<Signer> stringToSigner(String json) {
            if (TextUtils.isEmpty(json)) {
                return new ArrayList<>();
            }

            try {
                return signerAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for signer", e);
            }
        }

        @TypeConverter
        public static String signerToString(List<Signer> list) {
            return signerAdapter.toJson(list);
        }

        @TypeConverter
        public static Map<String, String> stringToData(String json) {
            if (TextUtils.isEmpty(json)) {
                return new HashMap<>();
            }

            try {
                return dataAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for data", e);
            }
        }

        @TypeConverter
        public static String dataToString(Map<String, String> data) {
            return dataAdapter.toJson(data);
        }
    }
}
