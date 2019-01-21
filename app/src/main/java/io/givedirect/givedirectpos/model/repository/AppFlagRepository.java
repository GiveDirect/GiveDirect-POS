package io.givedirect.givedirectpos.model.repository;

import android.arch.persistence.room.EmptyResultSetException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.givedirect.givedirectpos.EnvironmentConstants;
import io.givedirect.givedirectpos.model.api.GiveDirectApi;
import io.givedirect.givedirectpos.model.appflag.AppFlag;
import io.givedirect.givedirectpos.model.appflag.AppFlagDao;
import io.givedirect.givedirectpos.model.persistence.AppFlagDB;
import io.givedirect.givedirectpos.model.persistence.AppFlagDB.AppFlags;
import io.givedirect.givedirectpos.view.util.TextUtils;
import io.reactivex.Completable;
import io.reactivex.Single;

@Singleton
public class AppFlagRepository {
    @NonNull
    private final GiveDirectApi giveDirectApi;

    @NonNull
    private final AppFlagDao appFlagDao;

    @Nullable
    private String cachedMerchantAddress;

    @Nullable
    private String cachedAuthSeed;

    @Inject
    public AppFlagRepository(@NonNull GiveDirectApi giveDirectApi,
                             @NonNull AppFlagDB appFlagDB) {
        this.giveDirectApi = giveDirectApi;
        this.appFlagDao = appFlagDB.appFlagDao();
    }

    public Single<String> getMerchantAddress() {
        if (!TextUtils.isEmpty(cachedMerchantAddress)) {
            return Single.just(cachedMerchantAddress);
        }

        return appFlagDao.getFlagByKey(AppFlags.INITIALIZED_ADDRESS.name())
                .map(appFlag -> {
                    if (TextUtils.isEmpty(appFlag.getValue())) {
                        return "";
                    } else {
                        cachedMerchantAddress = appFlag.getValue();
                        return cachedMerchantAddress;
                    }
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyResultSetException) {
                        return Single.just("");
                    } else {
                        return Single.error(throwable);
                    }
                });
    }

    public Single<String> getAuthSeed() {
        if (!TextUtils.isEmpty(cachedAuthSeed)) {
            return Single.just(cachedAuthSeed);
        }

        return appFlagDao.getFlagByKey(AppFlags.INITIALIZED_SEED.name())
                .map(appFlag -> {
                    cachedAuthSeed = appFlag.getValue();
                    return cachedAuthSeed;
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyResultSetException) {
                        return getAuthSeedRemote();
                    } else {
                        return Single.error(throwable);
                    }
                });
    }

    private Single<String> getAuthSeedRemote() {
        return giveDirectApi.getAuthFile(EnvironmentConstants.GIVE_DIRECT_AUTH_FILE)
                .map(authFile -> {
                    cachedAuthSeed = authFile.getSeed();
                    return cachedAuthSeed;
                });
    }

    public Completable setMerchantAddress(@NonNull String merchantAddress) {
        return Completable.fromAction(() -> {
            AppFlag appFlag = new AppFlag();
            appFlag.setFlagKey(AppFlags.INITIALIZED_ADDRESS.name());
            appFlag.setValue(merchantAddress);
            appFlagDao.insert(appFlag);
            cachedMerchantAddress = merchantAddress;
        });
    }
}
