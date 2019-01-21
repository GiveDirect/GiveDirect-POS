package io.givedirect.givedirectpos.presenter.writenfc;

import android.support.annotation.NonNull;

import org.stellar.sdk.KeyPair;

import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import timber.log.Timber;

public class WriteNfcPresenter extends BasePresenter<WriteNfcContract.WriteNfcView>
        implements WriteNfcContract.WriteNfcPresenter {
    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean isGeneratingSeed = false;

    protected WriteNfcPresenter(@NonNull WriteNfcContract.WriteNfcView view,
                                @NonNull AppFlagRepository appFlagRepository,
                                @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.appFlagRepository = appFlagRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onNfcScanned() {
        if (isGeneratingSeed) {
            return;
        }

        isGeneratingSeed = true;

        appFlagRepository.getAuthSeed()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                    isGeneratingSeed = false;
                })
                .subscribe(authSeed -> {
                    KeyPair keyPair = KeyPair.random();
                    String encryptedSeed = NfcUtil.encryptSeed(new String(keyPair.getSecretSeed()),
                            authSeed);
                    view.writeWristband(encryptedSeed, keyPair.getAccountId());
                }, throwable -> {
                    Timber.e(throwable, "Failed to generate private key");
                    view.showWriteFailure();
                });
    }
}
