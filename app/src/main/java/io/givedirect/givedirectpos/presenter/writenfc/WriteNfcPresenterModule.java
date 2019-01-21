package io.givedirect.givedirectpos.presenter.writenfc;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;

@Module
public class WriteNfcPresenterModule {
    @Provides
    WriteNfcContract.WriteNfcPresenter provideWriteNfcPresenter(WriteNfcContract.WriteNfcView view,
                                                                AppFlagRepository appFlagRepository,
                                                                SchedulerProvider schedulerProvider) {
        return new WriteNfcPresenter(view, appFlagRepository, schedulerProvider);
    }
}
