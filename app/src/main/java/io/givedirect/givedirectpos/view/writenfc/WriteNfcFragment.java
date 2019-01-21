package io.givedirect.givedirectpos.view.writenfc;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.writenfc.WriteNfcContract;
import io.givedirect.givedirectpos.presenter.writenfc.WriteNfcFlowManager;
import io.givedirect.givedirectpos.view.common.BaseViewFragment;

public class WriteNfcFragment extends BaseViewFragment<WriteNfcContract.WriteNfcPresenter>
        implements WriteNfcContract.WriteNfcView {

    @BindView(R.id.write_success_container)
    ViewGroup successContainer;

    @BindView(R.id.write_success_pra)
    TextView praId;

    @BindView(R.id.write_success_address)
    TextView address;

    @BindView(R.id.write_failure_message)
    TextView failureMessage;

    public static WriteNfcFragment newInstance() {
        return new WriteNfcFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.write_nfc_fragment, container, false);
    }

    @Override
    public void showLoading(boolean isLoading) {
        ((WriteNfcFlowManager) activityContext).showLoading(isLoading);
    }

    @Override
    public void writeWristband(@NonNull String encryptedSeed,
                               @NonNull String publicAddress) {
        boolean successfulWrite = NfcUtil.bindNFCMessage(encryptedSeed,
                ((Activity) activityContext).getIntent());
        if (successfulWrite) {
            showWriteSuccess(publicAddress);
        } else {
            showWriteFailure();
        }
    }

    public void showWriteSuccess(@NonNull String address) {
        praId.setText(getString(R.string.pra_title, NfcUtil.getPraId(address)));
        this.address.setText(address);
        failureMessage.setVisibility(View.INVISIBLE);
        successContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showWriteFailure() {
        successContainer.setVisibility(View.INVISIBLE);
        failureMessage.setVisibility(View.VISIBLE);
    }

    public void onNFCScanned() {
        presenter.onNfcScanned();
    }
}
