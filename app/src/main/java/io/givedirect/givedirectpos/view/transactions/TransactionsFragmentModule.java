package io.givedirect.givedirectpos.view.transactions;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.transactions.TransactionsContract;
import io.givedirect.givedirectpos.presenter.transactions.TransactionsPresenterModule;

@Module(includes = {BaseFragmentModule.class, TransactionsPresenterModule.class})
public abstract class TransactionsFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TransactionsFragment transactionsFragment);

    @Binds
    @PerFragment
    abstract TransactionsContract.TransactionsView provideTransactionsView(TransactionsFragment transactionsFragment);
}
