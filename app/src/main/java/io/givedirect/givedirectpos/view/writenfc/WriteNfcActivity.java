package io.givedirect.givedirectpos.view.writenfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.presenter.writenfc.WriteNfcFlowManager;
import io.givedirect.givedirectpos.view.common.NfcActivity;
import io.givedirect.givedirectpos.view.common.ProgressOverlay;
import io.givedirect.givedirectpos.view.util.ViewUtils;

public class WriteNfcActivity extends NfcActivity implements WriteNfcFlowManager {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, WriteNfcActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setSupportActionBar(toolbar);

        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        });

        if (savedInstanceState == null) {
            startWriteNfcFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startWriteNfcFragment() {
        replaceFragment(
                R.id.fragment_container,
                WriteNfcFragment.newInstance(),
                true);
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void setupNFCReceiver() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcIntentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
        nfcTechs = new String[][]{new String[]{Ndef.class.getName(), NdefFormatable.class.getName()}};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof WriteNfcFragment) {
            setIntent(intent);
            ((WriteNfcFragment) fragment).onNFCScanned();
        }
    }
}
