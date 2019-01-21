package io.givedirect.givedirectpos.view.payment.transactionsummary;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.util.AssetUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil.GiveDirectNFCWallet;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.TransactionSummaryPresenter;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.TransactionSummaryView;
import io.givedirect.givedirectpos.view.common.BaseViewFragment;
import io.givedirect.givedirectpos.view.payment.PaymentFlowManager;
import timber.log.Timber;

public class TransactionSummaryFragment extends BaseViewFragment<TransactionSummaryPresenter>
        implements TransactionSummaryView {
    private static final String WALLET_ARG = "TransactionSummaryFragment.WALLET_ARG";
    private static final String CURRENT_BALANCE_ARG = "TransactionSummaryFragment.CURRENT_BALANCE_ARG";
    private static final String CHARGE_AMOUNT_ARG = "TransactionSummaryFragment.CHARGE_AMOUNT_ARG";

    @BindView(R.id.pra_title)
    TextView praTitle;

    @BindView(R.id.current_balance)
    TextView currentBalance;

    @BindView(R.id.charge_amount)
    TextView chargeAmount;

    @BindView(R.id.transaction_fee)
    TextView transactionFee;

    @BindView(R.id.remaining_balance)
    TextView remainingBalance;

    public static TransactionSummaryFragment newInstance(@NonNull GiveDirectNFCWallet wallet,
                                                         double currentBalance,
                                                         double chargeAmount) {
        Bundle args = new Bundle();
        args.putParcelable(WALLET_ARG, wallet);
        args.putDouble(CURRENT_BALANCE_ARG, currentBalance);
        args.putDouble(CHARGE_AMOUNT_ARG, chargeAmount);
        TransactionSummaryFragment fragment = new TransactionSummaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transaction_summary_fragment, container, false);
    }

    @NonNull
    @Override
    public TransactionSummaryContract.PendingTransactionInfo getPendingTransactionInfo() {
        Bundle args = getArguments();
        if (args == null) {
            Timber.e("Arguments were null");
            throw new IllegalStateException("Arguments cannot be null");
        }

        GiveDirectNFCWallet wallet = args.getParcelable(WALLET_ARG);

        if (wallet == null) {
            Timber.e("Wallet was null");
            throw new IllegalStateException("Wallet cannot be null");
        }

        return new TransactionSummaryContract.PendingTransactionInfo(
                wallet,
                args.getDouble(CURRENT_BALANCE_ARG),
                args.getDouble(CHARGE_AMOUNT_ARG));
    }

    @Override
    public void updateView(@NonNull String praId,
                           double oldBalance,
                           double chargeAmount,
                           double transactionFee,
                           double newBalance) {
        praTitle.setText(getString(R.string.pra_title, praId));
        this.currentBalance.setText(getLumensDisplay(oldBalance));
        this.chargeAmount.setText(getLumensDisplay(chargeAmount));
        this.transactionFee.setText(getLumensDisplay(transactionFee));
        this.remainingBalance.setText(getLumensDisplay(newBalance));
    }

    private String getLumensDisplay(double amount) {
        return getString(R.string.lumens_balance, AssetUtil.getAssetAmountString(amount));
    }

    @Override
    public void showBlockedLoading(boolean isProcessingTransaction) {
        int message = isProcessingTransaction ?
                R.string.processing_transaction
                : R.string.building_transaction;
        ((PaymentFlowManager) activityContext).showBlockedLoading(getString(message));
    }

    @Override
    public void hideBlockedLoading(boolean isProcessingTransaction, boolean wasSuccess) {
        if (isProcessingTransaction && wasSuccess) {
            ((PaymentFlowManager) activityContext).hideBlockedLoading(
                    getString(R.string.transaction_success),
                    true,
                    false,
                    this::completeTransaction);
        } else {
            ((PaymentFlowManager) activityContext).hideBlockedLoading(
                    null,
                    false,
                    true,
                    null);
        }
    }

    @Override
    public void showTransactionBuildError() {
        new AlertDialog.Builder(activityContext, R.style.WarningAlertDialog)
                .setTitle(R.string.failed_build_transaction_title)
                .setMessage(R.string.failed_build_transaction_message)
                .setPositiveButton(R.string.okay, ((dialog, which) -> {
                    dialog.dismiss();
                    ((Activity) activityContext).onBackPressed();
                }));
    }

    @Override
    public void showPotentiallyProcessedError() {
        new AlertDialog.Builder(activityContext, R.style.WarningAlertDialog)
                .setTitle(R.string.error)
                .setMessage(R.string.potentially_processed_message)
                .setPositiveButton(R.string.okay, ((dialog, which) -> completeTransaction()));
    }

    @Override
    public void showTransactionProcessError() {
        showToast(R.string.transaction_failed_message);
    }

    @Override
    public void showNfcParseError() {
        showToast(R.string.nfc_parse_error);
    }

    @Override
    public void showMalformedNfcError() {
        showToast(R.string.nfc_malformed_error);
    }

    private void showToast(@StringRes int message) {
        Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public GiveDirectNFCWallet parseNFC(@NonNull String authSeed) {
        return NfcUtil.parseNfcIntent(((Activity) activityContext).getIntent(), authSeed);
    }

    private void completeTransaction() {
        ((PaymentFlowManager) activityContext).onTransactionComplete(true);
    }

    public void onNFCScanned() {
        presenter.onUserScannedNFC();
    }
}
