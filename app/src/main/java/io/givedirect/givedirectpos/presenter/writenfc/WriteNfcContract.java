package io.givedirect.givedirectpos.presenter.writenfc;

import android.support.annotation.NonNull;

import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface WriteNfcContract {
    interface WriteNfcView {
        void showLoading(boolean isLoading);

        void writeWristband(@NonNull String encryptedSeed,
                            @NonNull String publicAddress);

        void showWriteFailure();
    }

    interface WriteNfcPresenter extends Presenter {
        void onNfcScanned();
    }
}
