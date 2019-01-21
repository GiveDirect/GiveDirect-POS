package io.givedirect.givedirectpos.model.persistence.givedirect;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.givedirect.givedirectpos.GiveDirectPOSApp;
import io.givedirect.givedirectpos.view.util.TextUtils;

@TypeConverters(AuthFile.AuthFileTypeConverters.class)
public class AuthFile {
    private String Seed;
    private Map<String, String> PubKey;

    public String getSeed() {
        return Seed;
    }

    public void setSeed(String seed) {
        Seed = seed;
    }

    public Map<String, String> getPubKey() {
        return PubKey;
    }

    public void setPubKey(Map<String, String> pubKey) {
        PubKey = pubKey;
    }

    public Map<String, String> getAddressWhitelist() {
        return PubKey;
    }

    public static class AuthFileTypeConverters {
        private static final JsonAdapter<Map<String, String>> whitelistAdapter;

        static {
            Moshi moshi = GiveDirectPOSApp.getInstance().getMoshi();
            whitelistAdapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
        }

        @TypeConverter
        public static Map<String, String> stringToPubKey(String json) {
            if (TextUtils.isEmpty(json)) {
                return new HashMap<>();
            }

            try {
                return whitelistAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for data", e);
            }
        }

        @TypeConverter
        public static String pubKeyToString(Map<String, String> data) {
            return whitelistAdapter.toJson(data);
        }
    }
}
