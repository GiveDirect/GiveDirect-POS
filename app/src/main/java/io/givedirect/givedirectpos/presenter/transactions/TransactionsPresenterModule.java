package io.givedirect.givedirectpos.presenter.transactions;

import com.squareup.moshi.Moshi;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import okhttp3.OkHttpClient;

@Module
public class TransactionsPresenterModule {
    @Provides
    TransactionsContract.TransactionsPresenter provideTransactionPresenter(
            TransactionsContract.TransactionsView view,
            AppFlagRepository appFlagRepository,
            HorizonApi horizonApi,
            OkHttpClient okHttpClient,
            Moshi moshi,
            SchedulerProvider schedulerProvider) {
        return new TransactionsPresenter(
                view,
                appFlagRepository,
                horizonApi,
                okHttpClient,
                moshi,
                schedulerProvider);
    }
}
