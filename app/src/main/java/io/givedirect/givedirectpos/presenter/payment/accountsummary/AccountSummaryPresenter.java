package io.givedirect.givedirectpos.presenter.payment.accountsummary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.persistence.account.Account;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.repository.FeesRepository;
import io.givedirect.givedirectpos.model.util.FeesUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import io.givedirect.givedirectpos.view.util.TextUtils;
import io.reactivex.Single;
import timber.log.Timber;

public class AccountSummaryPresenter
        extends BasePresenter<AccountSummaryContract.AccountSummaryView>
        implements AccountSummaryContract.AccountSummaryPresenter {
    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final FeesRepository feesRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private Account account;

    protected AccountSummaryPresenter(@NonNull AccountSummaryContract.AccountSummaryView view,
                                      @NonNull AccountRepository accountRepository,
                                      @NonNull AppFlagRepository appFlagRepository,
                                      @NonNull FeesRepository feesRepository,
                                      @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.accountRepository = accountRepository;
        this.appFlagRepository = appFlagRepository;
        this.schedulerProvider = schedulerProvider;
        this.feesRepository = feesRepository;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        NfcUtil.GiveDirectNFCWallet wallet = view.getWallet();
        Single.zip(accountRepository.getAccountByIdRemote(wallet.getPublicKey()),
                appFlagRepository.getMerchantAddress(),
                Pair::new)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(pair -> {
                    Account account = pair.first;

                    if (account == null) {
                        view.showLoadWalletError();
                        return;
                    }

                    this.account = account;

                    boolean canMakePayment = !TextUtils.isEmpty(pair.second);
                    double balance = account.isOnNetwork() ? account.getLumens().getBalance() : 0;

                    view.updateView(wallet.getPraID(),
                            wallet.getPublicKey(),
                            balance,
                            canMakePayment,
                            account.isOnNetwork());
                }, throwable -> {
                    Timber.e(throwable, "Failed to load wallet with address: %s", wallet.getPublicKey());
                    view.showLoadWalletError();
                });
    }

    @Override
    public void onUserRequestPayment(double chargeAmount) {
        if (chargeAmount <= 0) {
            view.showAmountGreaterThanZeroError();
            return;
        }

        if (account == null) {
            Timber.e("User requested payment before account loaded");
            return;
        }

        double balance = account.getLumens().getBalance();
        feesRepository.getFees(false)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(fees -> {
                    if (chargeAmount + FeesUtil.getTransactionFee(fees, 1) > balance) {
                        view.showInsufficientFundsError();
                    } else {
                        view.goToTransactionSummary(account, balance, chargeAmount);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to calculate fees");
                    view.showCalculationError();
                });
    }
}
