package io.givedirect.givedirectpos.presenter.home;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;

@Module
public class HomePresenterModule {
    @Provides
    HomeContract.HomePresenter provideHomePresenter(HomeContract.HomeView homeView,
                                                    AppFlagRepository appFlagRepository,
                                                    SchedulerProvider schedulerProvider) {
        return new HomePresenter(homeView, appFlagRepository, schedulerProvider);
    }
}
