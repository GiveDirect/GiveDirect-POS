package io.givedirect.givedirectpos.presenter.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import io.givedirect.givedirectpos.presenter.home.HomeContract.HomeView;
import io.givedirect.givedirectpos.view.util.TextUtils;
import io.givedirect.givedirectpos.view.util.ViewUtils;
import timber.log.Timber;

public class HomePresenter extends BasePresenter<HomeView> implements HomeContract.HomePresenter {
    private static final String INITIAL_SCREEN_SHOWN_KEY = "HomePresenter.INITIAL_SCREEN_SHOWN_KEY";

    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean hasShownInitialScreen = false;
    private boolean isLoading = false;

    protected HomePresenter(@NonNull HomeView view,
                            @NonNull AppFlagRepository appFlagRepository,
                            @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.appFlagRepository = appFlagRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        ViewUtils.whenNonNull(bundle, b ->
                hasShownInitialScreen = b.getBoolean(INITIAL_SCREEN_SHOWN_KEY));

        if (!hasShownInitialScreen) {
            checkMerchantAccountInitialized();
        }
    }

    private void checkMerchantAccountInitialized() {
        if (isLoading) {
            return;
        }

        isLoading = true;

        appFlagRepository.getMerchantAddress()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                    isLoading = false;
                })
                .subscribe(merchantAddress -> {
                    if (TextUtils.isEmpty(merchantAddress)) {
                        view.showSetupScreen();
                    } else {
                        view.showTransactionScreen();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to load merchant address");
                    view.showLoadError();
                });
    }

    @Override
    public void saveState(@Nullable Bundle bundle) {
        super.saveState(bundle);
        ViewUtils.whenNonNull(bundle, b ->
                b.putBoolean(INITIAL_SCREEN_SHOWN_KEY, hasShownInitialScreen));
    }

    @Override
    public void onMerchantAddressSet() {
        checkMerchantAccountInitialized();
    }

    @Override
    public void onUserScannedNFC() {
        appFlagRepository.getAuthSeed()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                })
                .subscribe(authSeed -> {
                    NfcUtil.GiveDirectNFCWallet wallet = view.parseNFC(authSeed);
                    if (wallet != null) {
                        if (!wallet.isValidFormat()) {
                            view.showMalformedNfcError();
                        } else {
                            view.startPaymentFlow(wallet);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to retrieve auth");
                    view.showNfcParseError();
                });
    }

    @Override
    public void onUserRetryLoad() {
        checkMerchantAccountInitialized();
    }

    @Override
    public void onUserRequestWriteWristband() {
        // Merchant address should be cached at this point, so won't bother with showing/hiding
        // loading state.
        appFlagRepository.getMerchantAddress()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(
                        address ->
                                view.startWriteWristbandFlow(!TextUtils.isEmpty(address)),
                        throwable ->
                                Timber.e(throwable, "Failed address check for write flow"));
    }

    @Override
    public void onUserRequestAbout() {
        view.startAboutFlow();
    }
}
