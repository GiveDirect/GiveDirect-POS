package io.givedirect.givedirectpos.view.home;

import android.support.annotation.Nullable;

import io.reactivex.functions.Action;

public interface SetupFlowManager {
    void onMerchantAddressSet();

    void showBlockedLoading(@Nullable String message);

    void hideBlockedLoading(@Nullable String message,
                            boolean wasSuccess,
                            boolean immediate,
                            @Nullable Action action);
}
