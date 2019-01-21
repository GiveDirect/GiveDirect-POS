package io.givedirect.givedirectpos.model.fees;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.model.persistence.EmbeddedHals;

public class FeesWrapper {
    @Nullable
    private EmbeddedHals<Fees> _embedded;

    @NonNull
    public Fees getFees() {
        return _embedded == null ? new Fees() : _embedded.getRecords()[0];
    }
}
