package io.givedirect.givedirectpos.model.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.fees.Fees;
import io.reactivex.Single;

@Singleton
public class FeesRepository {
    @Nullable
    private Fees fees;

    @NonNull
    private final HorizonApi horizonApi;

    @Inject
    public FeesRepository(@NonNull HorizonApi horizonApi) {
        this.horizonApi = horizonApi;
    }

    public Single<Fees> getFees(boolean deepPoll) {
        if (!deepPoll && fees != null) {
            return Single.just(fees);
        }

        return horizonApi.getFees()
                .map(feesWrapper -> {
                    this.fees = feesWrapper.getFees();
                    return fees;
                });
    }
}
