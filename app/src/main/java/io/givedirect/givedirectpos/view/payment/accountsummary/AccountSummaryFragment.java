package io.givedirect.givedirectpos.view.payment.accountsummary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.persistence.account.Account;
import io.givedirect.givedirectpos.model.util.AssetUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.payment.accountsummary.AccountSummaryContract;
import io.givedirect.givedirectpos.view.common.BaseViewFragment;
import io.givedirect.givedirectpos.view.payment.PaymentFlowManager;
import io.givedirect.givedirectpos.view.util.ViewUtils;

public class AccountSummaryFragment
        extends BaseViewFragment<AccountSummaryContract.AccountSummaryPresenter>
        implements AccountSummaryContract.AccountSummaryView {
    private static final String WALLET_ARG = "AccountSummaryFragment.WALLET_ARG";

    @BindView(R.id.pra_label)
    TextView praLabel;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.balance)
    TextView balance;

    @BindView(R.id.qr_code)
    ImageView qrCode;

    @BindView(R.id.amount_field_layout)
    TextInputLayout amountFieldLayout;

    @BindView(R.id.amount_field)
    TextInputEditText amountField;

    @BindView(R.id.payment_btn)
    Button paymentButton;

    @BindView(R.id.cannot_process_charge_message)
    TextView cannotProcessChargeMessage;

    public static AccountSummaryFragment newInstance(@NonNull NfcUtil.GiveDirectNFCWallet wallet) {
        Bundle args = new Bundle();
        args.putParcelable(WALLET_ARG, wallet);
        AccountSummaryFragment fragment = new AccountSummaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_summary_fragment, container, false);
    }

    @NonNull
    @Override
    public NfcUtil.GiveDirectNFCWallet getWallet() {
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("Arguments cannot be null");
        }
        NfcUtil.GiveDirectNFCWallet wallet = args.getParcelable(WALLET_ARG);
        if (wallet == null) {
            throw new IllegalStateException("Wallet cannot be null");
        }

        return wallet;
    }

    @Override
    public void updateView(@NonNull String praId,
                           @NonNull String accountId,
                           double balance,
                           boolean canMakePayment,
                           boolean accountOnNetwork) {
        praLabel.setText(getString(R.string.pra_label, praId));
        address.setText(accountId);
        this.balance.setText(getString(R.string.lumens_balance,
                AssetUtil.getAssetAmountString(balance)));
        qrCode.setImageBitmap(ViewUtils.encodeAsBitmap(accountId));
        amountFieldLayout.setVisibility(canMakePayment && accountOnNetwork ? View.VISIBLE : View.GONE);
        paymentButton.setVisibility(canMakePayment && accountOnNetwork ? View.VISIBLE : View.GONE);
        if (!canMakePayment) {
            cannotProcessChargeMessage.setText(R.string.init_address_message);
            cannotProcessChargeMessage.setVisibility(View.VISIBLE);
        } else if (!accountOnNetwork) {
            cannotProcessChargeMessage.setText(R.string.account_not_initialized_message);
            cannotProcessChargeMessage.setVisibility(View.VISIBLE);
        } else {
            cannotProcessChargeMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void goToTransactionSummary(Account account,
                                       double currentBalance,
                                       double chargeAmount) {
        ((PaymentFlowManager) activityContext).goToTransactionSummary(currentBalance, chargeAmount);
    }

    @Override
    public void showInsufficientFundsError() {
        amountFieldLayout.setError(getString(R.string.insufficient_funds_error));
    }

    @Override
    public void showAmountGreaterThanZeroError() {
        amountFieldLayout.setError(getString(R.string.greater_than_zero_error));
    }

    @Override
    public void showLoadWalletError() {
        Toast.makeText(activityContext,
                R.string.account_load_fail_message,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void showCalculationError() {
        Toast.makeText(activityContext,
                R.string.fee_calculation_fail_message,
                Toast.LENGTH_LONG)
                .show();
    }

    @OnTextChanged(R.id.amount_field)
    public void onAmountFieldContentsChanged(Editable editable) {
        amountFieldLayout.setError(null);
    }

    @OnClick(R.id.payment_btn)
    public void onPaymentRequested() {
        ((PaymentFlowManager) activityContext).hideKeyboard();

        String amount = amountField.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            amountFieldLayout.setError(getString(R.string.empty_amount_error));
        } else {
            presenter.onUserRequestPayment(Double.parseDouble(amount));
        }
    }

    @Override
    public void showLoading(boolean isLoading) {
        ((PaymentFlowManager) activityContext).showLoading(isLoading);
    }
}
