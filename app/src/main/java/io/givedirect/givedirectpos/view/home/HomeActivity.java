package io.givedirect.givedirectpos.view.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.home.HomeContract;
import io.givedirect.givedirectpos.view.about.AboutActivity;
import io.givedirect.givedirectpos.view.common.NfcActivity;
import io.givedirect.givedirectpos.view.common.ProgressOverlay;
import io.givedirect.givedirectpos.view.payment.PaymentActivity;
import io.givedirect.givedirectpos.view.setup.SetupFragment;
import io.givedirect.givedirectpos.view.transactions.TransactionsFragment;
import io.givedirect.givedirectpos.view.util.ViewUtils;
import io.givedirect.givedirectpos.view.writenfc.WriteNfcActivity;
import io.reactivex.functions.Action;
import timber.log.Timber;

public class HomeActivity extends NfcActivity implements HomeContract.HomeView, SetupFlowManager,
        NavigationView.OnNavigationItemSelectedListener {
    private static final int TRANSACTION_COMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @Inject
    HomeContract.HomePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);
        setupActionBar();
        navigationView.setNavigationItemSelectedListener(this);
        presenter.start(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveState(outState);
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

        setIntent(intent);
        presenter.onUserScannedNFC();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSetupScreen() {
        replaceFragment(R.id.fragment_container, new SetupFragment(), true);
    }

    @Override
    public void showTransactionScreen() {
        replaceFragment(R.id.fragment_container, new TransactionsFragment(), true);
    }

    @Override
    public void startPaymentFlow(@NonNull NfcUtil.GiveDirectNFCWallet giveDirectNFCWallet) {
        startActivityForResult(PaymentActivity.createIntent(this, giveDirectNFCWallet),
                TRANSACTION_COMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TRANSACTION_COMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshTransactions();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshTransactions() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof TransactionsFragment) {
            ((TransactionsFragment) fragment).refreshTransactions();
        }
    }

    @Override
    public void showLoadError() {
        new AlertDialog.Builder(this, R.style.WarningAlertDialog)
                .setTitle(R.string.load_home_error_title)
                .setMessage(R.string.load_home_error_message)
                .setPositiveButton(R.string.retry, (dialog, which) -> presenter.onUserRetryLoad())
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void showNfcParseError() {
        Toast.makeText(this, R.string.nfc_parse_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMalformedNfcError() {
        Toast.makeText(this, R.string.nfc_malformed_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startWriteWristbandFlow(boolean hasInitializedMerchantAddress) {
        if (hasInitializedMerchantAddress) {
            startActivity(WriteNfcActivity.createIntent(this));
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.address_init_required_for_write)
                    .setPositiveButton(R.string.okay, null)
                    .setCancelable(true)
                    .create()
                    .show();
        }
    }

    @Override
    public void startAboutFlow() {
        startActivity(AboutActivity.createIntent(this));
    }

    @Override
    public NfcUtil.GiveDirectNFCWallet parseNFC(@NonNull String authSeed) {
        return NfcUtil.parseNfcIntent(getIntent(), authSeed);
    }

    @Override
    public void onMerchantAddressSet() {
        presenter.onMerchantAddressSet();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.write_wristband) {
            presenter.onUserRequestWriteWristband();
        } else if (item.getItemId() == R.id.about) {
            presenter.onUserRequestAbout();
        }

        drawerLayout.closeDrawers();
        return false;
    }
}
