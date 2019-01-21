package io.givedirect.givedirectpos.view.payment;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.givedirect.givedirectpos.dagger.module.BaseActivityModule;
import io.givedirect.givedirectpos.dagger.scope.PerActivity;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.view.payment.accountsummary.AccountSummaryFragment;
import io.givedirect.givedirectpos.view.payment.accountsummary.AccountSummaryFragmentModule;
import io.givedirect.givedirectpos.view.payment.transactionsummary.TransactionSummaryFragment;
import io.givedirect.givedirectpos.view.payment.transactionsummary.TransactionSummaryFragmentModule;

@Module(includes = BaseActivityModule.class)
public abstract class PaymentActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(PaymentActivity paymentActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = AccountSummaryFragmentModule.class)
    abstract AccountSummaryFragment accountSummaryFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = TransactionSummaryFragmentModule.class)
    abstract TransactionSummaryFragment transactionSummaryFragmentInjector();
}
