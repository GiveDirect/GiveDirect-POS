package io.givedirect.givedirectpos.presenter.setup;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.GiveDirectApi;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;

@Module
public class SetupPresenterModule {
    @Provides
    SetupContract.SetupPresenter provideSetupPresenter(SetupContract.SetupView setupView,
                                                       AccountRepository accountRepository,
                                                       AppFlagRepository appFlagRepository,
                                                       GiveDirectApi giveDirectApi,
                                                       SchedulerProvider schedulerProvider) {
        return new SetupPresenter(
                setupView,
                accountRepository,
                appFlagRepository,
                giveDirectApi,
                schedulerProvider);
    }
}
