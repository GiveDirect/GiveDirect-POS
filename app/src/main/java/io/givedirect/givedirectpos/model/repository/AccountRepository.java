package io.givedirect.givedirectpos.model.repository;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.persistence.account.Account;
import io.givedirect.givedirectpos.model.persistence.account.DisconnectedAccount;
import io.reactivex.Single;
import retrofit2.HttpException;

@Singleton
public class AccountRepository {

    @NonNull
    private final HorizonApi horizonApi;

    @Inject
    public AccountRepository(@NonNull HorizonApi horizonApi) {
        this.horizonApi = horizonApi;
    }

    public Single<Account> getAccountByIdRemote(@NonNull String accountId) {
        return horizonApi.getAccount(accountId)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException
                            && ((HttpException) throwable).code() == 404) {
                        return Single.just(new DisconnectedAccount(accountId));
                    }

                    return Single.error(throwable);
                });
    }
}
