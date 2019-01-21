package io.givedirect.givedirectpos.model.appflag;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class AppFlag {
    @NonNull
    @PrimaryKey
    private String flagKey = "";

    private String value;

    @NonNull
    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(@NonNull String flagKey) {
        this.flagKey = flagKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
