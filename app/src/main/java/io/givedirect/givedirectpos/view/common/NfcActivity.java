package io.givedirect.givedirectpos.view.common;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.R;
import timber.log.Timber;

public abstract class NfcActivity extends BaseActivity {
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    protected IntentFilter[] nfcIntentFilters;
    protected String[][] nfcTechs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNFCReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(
                    this,
                    nfcPendingIntent,
                    nfcIntentFilters,
                    nfcTechs);
        } else {
            new AlertDialog.Builder(this, R.style.WarningAlertDialog)
                    .setTitle(R.string.nfc_unavailable_title)
                    .setMessage(R.string.nfc_unavailable_message)
                    .setPositiveButton(R.string.okay, null)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    protected void setupNFCReceiver() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Timber.e(e, "Failed to set NFC intent filter mime type");
        }
        nfcIntentFilters = new IntentFilter[]{ndef};
        nfcTechs = new String[][]{new String[]{NfcF.class.getName()}};
    }
}
