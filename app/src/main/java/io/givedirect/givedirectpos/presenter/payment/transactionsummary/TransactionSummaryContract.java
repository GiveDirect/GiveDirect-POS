package io.givedirect.givedirectpos.presenter.payment.transactionsummary;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface TransactionSummaryContract {
    interface TransactionSummaryView {
        @NonNull
        PendingTransactionInfo getPendingTransactionInfo();

        void updateView(@NonNull String praId,
                        double oldBalance,
                        double chargeAmount,
                        double transactionFee,
                        double newBalance);

        void showBlockedLoading(boolean isProcessingTransaction);

        void hideBlockedLoading(boolean isProcessingTransaction,
                                boolean wasSuccess);

        void showTransactionBuildError();

        void showPotentiallyProcessedError();

        void showTransactionProcessError();

        void showNfcParseError();

        void showMalformedNfcError();

        @Nullable
        NfcUtil.GiveDirectNFCWallet parseNFC(@NonNull String authSeed);
    }

    interface TransactionSummaryPresenter extends Presenter {
        void onUserScannedNFC();
    }

    class PendingTransactionInfo {
        @NonNull
        public final NfcUtil.GiveDirectNFCWallet nfcWallet;

        public final double currentBalance;

        public final double chargeAmount;


        public PendingTransactionInfo(@NonNull NfcUtil.GiveDirectNFCWallet nfcWallet,
                                      double currentBalance,
                                      double chargeAmount) {
            this.nfcWallet = nfcWallet;
            this.currentBalance = currentBalance;
            this.chargeAmount = chargeAmount;
        }
    }
}
