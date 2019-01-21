package io.givedirect.givedirectpos.view.payment.transactionsummary;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.TransactionSummaryView;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryPresenterModule;

@Module(includes = {BaseFragmentModule.class, TransactionSummaryPresenterModule.class})
public abstract class TransactionSummaryFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TransactionSummaryFragment transactionSummaryFragment);

    @Binds
    @PerFragment
    abstract TransactionSummaryView provideTransactionSummaryView(TransactionSummaryFragment transactionSummaryFragment);
}
