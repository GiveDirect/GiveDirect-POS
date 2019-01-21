package io.givedirect.givedirectpos.view.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.presenter.setup.SetupContract;
import io.givedirect.givedirectpos.view.common.BaseViewFragment;
import io.givedirect.givedirectpos.view.home.SetupFlowManager;

public class SetupFragment extends BaseViewFragment<SetupContract.SetupPresenter>
        implements SetupContract.SetupView {

    @BindView(R.id.recipient_field_layout)
    TextInputLayout textInputLayout;

    @BindView(R.id.recipient_field)
    TextInputEditText textInputEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setup_fragment, container, false);
    }

    @Override
    public void showLookupInProgress(boolean isInProgress) {
        if (isInProgress) {
            ((SetupFlowManager) activityContext)
                    .showBlockedLoading(getString(R.string.receiving_address_lookup_progress));
        } else {
            ((SetupFlowManager) activityContext).hideBlockedLoading(null, true, true, null);
        }
    }

    @OnTextChanged(R.id.recipient_field)
    public void onRecipientContentsChanged(Editable editable) {
        textInputLayout.setError(null);
    }

    @Override
    public void showSetupError(@NonNull SetupContract.SetupPresenter.SetupError setupError) {
        switch(setupError) {
            case INVALID_LENGTH:
                textInputLayout.setError(getString(R.string.receiving_address_length_error));
                break;
            case INVALID_PREFIX:
                textInputLayout.setError(getString(R.string.receiving_address_prefix_error));
                break;
            case INVALID_ADDRESS:
                textInputLayout.setError(getString(R.string.receiving_address_invalid_error));
                break;
            case UNKNOWN_ERROR:
                showErrorAlert(R.string.receiving_address_unknown_error_title,
                        R.string.receiving_address_unknown_error_message);
                break;
            case NOT_WHITELISTED:
                showErrorAlert(R.string.receiving_address_not_whitelisted_title,
                        R.string.receiving_address_not_whitelisted_message);
                break;
            case UNINITIALIZED_ACCOUNT:
                showErrorAlert(R.string.receiving_address_uninitialized_error_title,
                        R.string.receiving_address_uninitialized_error_message);
                break;
        }
    }

    private void showErrorAlert(@StringRes int titleRes,
                                @StringRes int messageRes) {
        new AlertDialog.Builder(activityContext, R.style.WarningAlertDialog)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(R.string.okay, null)
                .setCancelable(true)
                .show();
    }

    @OnClick(R.id.camera_btn)
    public void onUserRequestQRScanner() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                textInputEditText.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.set_address_btn)
    public void onUserRequestSetAddress() {
        textInputLayout.setError(null);
        String address = textInputEditText.getText().toString().toUpperCase();
        textInputEditText.setText(address);
        presenter.onUserEnterAddress(address);
    }

    @Override
    public void proceedNext() {
        ((SetupFlowManager) activityContext).onMerchantAddressSet();
    }
}
