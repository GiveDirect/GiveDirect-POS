package io.givedirect.givedirectpos.view.payment.accountsummary;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.payment.accountsummary.AccountSummaryContract;
import io.givedirect.givedirectpos.presenter.payment.accountsummary.AccountSummaryPresenterModule;

@Module(includes = {BaseFragmentModule.class, AccountSummaryPresenterModule.class})
public abstract class AccountSummaryFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(AccountSummaryFragment accountSummaryFragment);

    @Binds
    @PerFragment
    abstract AccountSummaryContract.AccountSummaryView provideAccountSummaryView(AccountSummaryFragment accountSummaryFragment);
}
