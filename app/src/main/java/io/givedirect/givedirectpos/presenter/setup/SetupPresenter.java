package io.givedirect.givedirectpos.presenter.setup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import io.givedirect.givedirectpos.EnvironmentConstants;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.GiveDirectApi;
import io.givedirect.givedirectpos.model.persistence.account.Account;
import io.givedirect.givedirectpos.model.persistence.givedirect.AuthFile;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.util.AccountUtil;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

public class SetupPresenter extends BasePresenter<SetupContract.SetupView>
        implements SetupContract.SetupPresenter {
    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final GiveDirectApi giveDirectApi;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    protected SetupPresenter(@NonNull SetupContract.SetupView view,
                             @NonNull AccountRepository accountRepository,
                             @NonNull AppFlagRepository appFlagRepository,
                             @NonNull GiveDirectApi giveDirectApi,
                             @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.accountRepository = accountRepository;
        this.appFlagRepository = appFlagRepository;
        this.giveDirectApi = giveDirectApi;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onUserEnterAddress(@Nullable String address) {
        view.showLookupInProgress(true);
        boolean errorEncountered = false;

        if (!AccountUtil.publicKeyOfProperLength(address)) {
            view.showSetupError(SetupError.INVALID_LENGTH);
            errorEncountered = true;
        } else if (!AccountUtil.publicKeyOfProperPrefix(address)) {
            view.showSetupError(SetupError.INVALID_PREFIX);
            errorEncountered = true;
        } else if (!AccountUtil.publicKeyCanBeDecoded(address)) {
            view.showSetupError(SetupError.INVALID_ADDRESS);
            errorEncountered = true;
        }

        if (errorEncountered) {
            view.showLookupInProgress(false);
            return;
        }

        initializeMerchantAddress(address);
    }

    private void initializeMerchantAddress(@NonNull String address) {
        lookupAccount(address)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(this::addDisposable)
                .flatMapCompletable(this::setMerchantAccount)
                .doAfterTerminate(() -> view.showLookupInProgress(false))
                .subscribe(view::proceedNext,
                        throwable -> {
                            Timber.e(throwable, "Error for address %s", address);
                            if (throwable instanceof NotOnNetworkException) {
                                view.showSetupError(SetupError.UNINITIALIZED_ACCOUNT);
                            } else if (throwable instanceof NotWhitelistedException) {
                                view.showSetupError(SetupError.NOT_WHITELISTED);
                            } else {
                                view.showSetupError(SetupError.UNKNOWN_ERROR);
                            }
                        });
    }

    private Single<Account> lookupAccount(@NonNull String address) {
        return Single.zip(
                accountRepository.getAccountByIdRemote(address),
                giveDirectApi.getAuthFile(EnvironmentConstants.GIVE_DIRECT_AUTH_FILE),
                (Pair::new))
                .flatMap(accountAuthFilePair -> {
                    AuthFile authFile = accountAuthFilePair.second;
                    Account account = accountAuthFilePair.first;

                    if (!authFile.getAddressWhitelist()
                            .containsValue(account.getAccount_id())) {
                        return Single.error(new NotWhitelistedException());
                    } else if (!account.isOnNetwork()) {
                        return Single.error(new NotOnNetworkException());
                    } else {
                        return Single.just(account);
                    }
                });
    }

    private Completable setMerchantAccount(@NonNull Account account) {
        return appFlagRepository.setMerchantAddress(account.getAccount_id())
                .compose(schedulerProvider.completableScheduler());
    }

    private class NotOnNetworkException extends Exception {
    }

    private class NotWhitelistedException extends Exception {
    }
}
