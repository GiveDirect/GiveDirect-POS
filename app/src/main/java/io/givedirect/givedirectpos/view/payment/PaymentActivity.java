package io.givedirect.givedirectpos.view.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.view.common.NfcActivity;
import io.givedirect.givedirectpos.view.common.ProgressOverlay;
import io.givedirect.givedirectpos.view.payment.accountsummary.AccountSummaryFragment;
import io.givedirect.givedirectpos.view.payment.transactionsummary.TransactionSummaryFragment;
import io.givedirect.givedirectpos.view.util.ViewUtils;
import io.reactivex.functions.Action;
import timber.log.Timber;

public class PaymentActivity extends NfcActivity implements PaymentFlowManager {
    private static final String NFC_WALLET_KEY = "PaymentActivity.NFC_WALLET_KEY";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.fragment_container)
    View fragmentContainer;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private NfcUtil.GiveDirectNFCWallet wallet;

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull NfcUtil.GiveDirectNFCWallet wallet) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(NFC_WALLET_KEY, wallet);
        return intent;
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

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Timber.e("Extras cannot be null");
            finish();
        } else {
            wallet = extras.getParcelable(NFC_WALLET_KEY);
            if (wallet == null) {
                Timber.e("Wallet was empty");
                finish();
            }
        }

        if (savedInstanceState == null) {
            startAccountSummaryFragment();
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

    private void startAccountSummaryFragment() {
        replaceFragment(
                R.id.fragment_container,
                AccountSummaryFragment.newInstance(wallet),
                true);
    }

    @Override
    public void goToTransactionSummary(double currentBalance, double chargeAmount) {
        replaceFragment(
                R.id.fragment_container,
                TransactionSummaryFragment.newInstance(wallet, currentBalance, chargeAmount),
                true,
                TransactionSummaryFragment.class.getSimpleName());
    }

    @Override
    public void onTransactionComplete(boolean wasSuccessful) {
        setResult(wasSuccessful ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveNFCIntent(intent);
    }

    private void resolveNFCIntent(@Nullable Intent intent) {
        if (intent == null) {
            Timber.d("Intent was empty");
            return;
        }

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof TransactionSummaryFragment) {
            setIntent(intent);
            ((TransactionSummaryFragment) fragment).onNFCScanned();
        }
    }

    @Override
    public void showBlockedLoading(@Nullable String message) {
        progressOverlay.show(message);
    }

    @Override
    public void hideBlockedLoading(@Nullable String message,
                                   boolean wasSuccess,
                                   boolean immediate,
                                   @Nullable Action action) {
        progressOverlay.hide(message, wasSuccess, immediate, action);
    }

    @Override
    public void hideKeyboard() {
        try {
            ViewUtils.whenNonNull(getCurrentFocus(), view -> {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), 0);
            });
        } catch (Exception e) {
            Timber.e(e, "Failed to hide keyboard");
        }
    }
}
