package io.givedirect.givedirectpos.presenter.payment.transactionsummary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Transaction;

import java.io.IOException;

import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.fees.Fees;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.repository.FeesRepository;
import io.givedirect.givedirectpos.model.util.FeesUtil;
import io.givedirect.givedirectpos.model.util.NfcUtil.GiveDirectNFCWallet;
import io.givedirect.givedirectpos.model.util.TransactionUtil;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.PendingTransactionInfo;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.TransactionSummaryView;
import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

public class TransactionSummaryPresenter
        extends BasePresenter<TransactionSummaryView>
        implements TransactionSummaryContract.TransactionSummaryPresenter {
    private static final String LAST_KNOWN_SEQUENCE_KEY = "TransactionsSummaryPresenter.LAST_KNOWN_SEQUENCE_KEY";

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final FeesRepository feesRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private Transaction pendingTransaction;

    private long lastKnownSequenceNumber = -1;

    protected TransactionSummaryPresenter(@NonNull TransactionSummaryView view,
                                          @NonNull HorizonApi horizonApi,
                                          @NonNull AppFlagRepository appFlagRepository,
                                          @NonNull AccountRepository accountRepository,
                                          @NonNull FeesRepository feesRepository,
                                          @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.appFlagRepository = appFlagRepository;
        this.accountRepository = accountRepository;
        this.feesRepository = feesRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);

        if (bundle != null) {
            lastKnownSequenceNumber = bundle.getLong(LAST_KNOWN_SEQUENCE_KEY, -1);
        }

        PendingTransactionInfo paymentInfo = view.getPendingTransactionInfo();

        getPaymentOperation(paymentInfo.nfcWallet, paymentInfo.chargeAmount)
                .flatMap(operation -> getTransaction(paymentInfo.nfcWallet, operation))
                .zipWith(feesRepository.getFees(false), Pair::new)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showBlockedLoading(false);
                })
                .subscribe(pair -> {
                    updateView(paymentInfo, pair);
                    view.hideBlockedLoading(false, true);
                }, throwable -> {
                    view.hideBlockedLoading(false, false);
                    Timber.e(throwable, "Failed to build transaction");
                    if (throwable instanceof SequenceMismatchException) {
                        view.showPotentiallyProcessedError();
                    } else {
                        view.showTransactionBuildError();
                    }
                });
    }

    @Override
    public void saveState(@Nullable Bundle bundle) {
        super.saveState(bundle);
        if (bundle != null) {
            bundle.putLong(LAST_KNOWN_SEQUENCE_KEY, lastKnownSequenceNumber);
        }
    }

    private void updateView(@NonNull PendingTransactionInfo paymentInfo,
                            @NonNull Pair<Transaction, Fees> pair) {
        pendingTransaction = pair.first;
        double curBalance = paymentInfo.currentBalance;
        double chargeAmount = paymentInfo.chargeAmount;
        double transactionFee = FeesUtil.getTransactionFee(pair.second, 1);
        double newBalance = curBalance - chargeAmount - transactionFee;
        view.updateView(paymentInfo.nfcWallet.getPraID(),
                curBalance,
                chargeAmount,
                transactionFee,
                newBalance);
    }

    private Single<PaymentOperation> getPaymentOperation(@NonNull GiveDirectNFCWallet wallet,
                                                         double sendAmount) {
        return appFlagRepository.getMerchantAddress()
                .compose(schedulerProvider.singleScheduler())
                .map(merchantAddress ->
                        new PaymentOperation.Builder(
                                KeyPair.fromAccountId(merchantAddress),
                                new AssetTypeNative(),
                                String.valueOf(sendAmount))
                                .setSourceAccount(KeyPair.fromAccountId(wallet.getPublicKey()))
                                .build());
    }

    private Single<Transaction> getTransaction(@NonNull GiveDirectNFCWallet wallet,
                                               @NonNull PaymentOperation operation) {
        return accountRepository.getAccountByIdRemote(wallet.getPublicKey())
                .compose(schedulerProvider.singleScheduler())
                .map(account -> {
                    if (lastKnownSequenceNumber > 0) {
                        if (lastKnownSequenceNumber != account.getIncrementedSequenceNumber()) {
                            throw new SequenceMismatchException();
                        }
                    } else {
                        lastKnownSequenceNumber = account.getIncrementedSequenceNumber();
                    }

                    return new Transaction.Builder(account)
                            .addOperation(operation)
                            .build();
                });
    }

    @Override
    public void onUserScannedNFC() {
        if (pendingTransaction == null) {
            Timber.e("User scanned wallet before transaction prepared");
            return;
        }

        appFlagRepository.getAuthSeed()
                .compose(schedulerProvider.singleScheduler())
                .flatMapCompletable(authSeed -> {
                    GiveDirectNFCWallet scannedWallet = view.parseNFC(authSeed);
                    if (scannedWallet == null) {
                        return Completable.error(new NFCParseException());
                    } else if (!scannedWallet.isValidFormat()) {
                        return Completable.error(new NFCMalformedException());
                    } else {
                        return postTransaction(scannedWallet);
                    }
                })
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showBlockedLoading(true);
                })
                .subscribe(() ->
                                view.hideBlockedLoading(true, true),
                        throwable -> {
                            view.hideBlockedLoading(true, false);
                            if (throwable instanceof NFCParseException) {
                                view.showNfcParseError();
                            } else if (throwable instanceof NFCMalformedException) {
                                view.showMalformedNfcError();
                            } else {
                                view.showTransactionProcessError();
                            }
                        });
    }

    private Completable postTransaction(@NonNull GiveDirectNFCWallet nfcWallet) throws IOException {
        pendingTransaction.sign(KeyPair.fromSecretSeed(nfcWallet.getPrivateKey()));

        return horizonApi.postTransaction(TransactionUtil.getEnvelopeXDRBase64(pendingTransaction))
                .compose(schedulerProvider.completableScheduler());
    }

    private static class SequenceMismatchException extends Exception {
    }

    private static class NFCParseException extends Exception {
    }

    private static class NFCMalformedException extends Exception {
    }
}
