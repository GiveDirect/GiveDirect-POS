package io.givedirect.givedirectpos.presenter.payment.accountsummary;

import android.support.annotation.NonNull;

import io.givedirect.givedirectpos.model.persistence.account.Account;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface AccountSummaryContract {
    interface AccountSummaryView {
        @NonNull
        NfcUtil.GiveDirectNFCWallet getWallet();

        void updateView(@NonNull String praId,
                        @NonNull String accountId,
                        double balance,
                        boolean canMakePayment,
                        boolean accountOnNetwork);

        void goToTransactionSummary(Account account, double currentBalance, double chargeAmount);

        void showInsufficientFundsError();

        void showAmountGreaterThanZeroError();

        void showLoadWalletError();

        void showCalculationError();

        void showLoading(boolean isLoading);
    }

    interface AccountSummaryPresenter extends Presenter {
        void onUserRequestPayment(double chargeAmount);
    }
}
