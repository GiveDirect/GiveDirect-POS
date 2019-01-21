package io.givedirect.givedirectpos.presenter.setup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface SetupContract {
    interface SetupView {
        void showLookupInProgress(boolean isInProgress);
        void showSetupError(@NonNull SetupPresenter.SetupError setupError);
        void proceedNext();
    }

    interface SetupPresenter extends Presenter {
        enum SetupError {
            INVALID_LENGTH,
            INVALID_PREFIX,
            INVALID_ADDRESS,
            UNINITIALIZED_ACCOUNT,
            NOT_WHITELISTED,
            UNKNOWN_ERROR
        }

        void onUserEnterAddress(@Nullable String address);
    }
}
