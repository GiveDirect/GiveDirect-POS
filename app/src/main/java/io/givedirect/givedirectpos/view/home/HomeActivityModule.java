package io.givedirect.givedirectpos.view.home;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.givedirect.givedirectpos.dagger.module.BaseActivityModule;
import io.givedirect.givedirectpos.dagger.scope.PerActivity;
import io.givedirect.givedirectpos.dagger.scope.PerFragment;
import io.givedirect.givedirectpos.presenter.home.HomeContract;
import io.givedirect.givedirectpos.presenter.home.HomePresenterModule;
import io.givedirect.givedirectpos.view.setup.SetupFragment;
import io.givedirect.givedirectpos.view.setup.SetupFragmentModule;
import io.givedirect.givedirectpos.view.transactions.TransactionsFragment;
import io.givedirect.givedirectpos.view.transactions.TransactionsFragmentModule;

@Module (includes = {BaseActivityModule.class, HomePresenterModule.class})
public abstract class HomeActivityModule {
    @Binds
    @PerActivity
    abstract HomeContract.HomeView provideHomeView(HomeActivity homeActivity);

    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(HomeActivity homeActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = SetupFragmentModule.class)
    abstract SetupFragment setupFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = TransactionsFragmentModule.class)
    abstract TransactionsFragment transactionsFragmentInjector();
}
