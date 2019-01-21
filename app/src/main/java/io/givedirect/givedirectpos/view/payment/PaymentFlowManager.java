package io.givedirect.givedirectpos.view.payment;

import android.support.annotation.Nullable;

import io.reactivex.functions.Action;

public interface PaymentFlowManager {
    void goToTransactionSummary(double currentBalance, double chargeAmount);

    void onTransactionComplete(boolean wasSuccessful);

    void showLoading(boolean isLoading);

    void showBlockedLoading(@Nullable String message);

    void hideBlockedLoading(@Nullable String message,
                            boolean wasSuccess,
                            boolean immediate,
                            @Nullable Action action);

    void hideKeyboard();
}
