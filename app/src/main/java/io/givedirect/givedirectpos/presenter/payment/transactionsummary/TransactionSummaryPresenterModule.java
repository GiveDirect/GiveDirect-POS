package io.givedirect.givedirectpos.presenter.payment.transactionsummary;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.repository.FeesRepository;
import io.givedirect.givedirectpos.presenter.payment.transactionsummary.TransactionSummaryContract.TransactionSummaryView;

@Module
public class TransactionSummaryPresenterModule {
    @Provides
    public TransactionSummaryContract.TransactionSummaryPresenter provideTransactionSummaryPresenter(TransactionSummaryView view,
                                                                                                     HorizonApi horizonApi,
                                                                                                     AppFlagRepository appFlagRepository,
                                                                                                     AccountRepository accountRepository,
                                                                                                     FeesRepository feesRepository,
                                                                                                     SchedulerProvider schedulerProvider) {
        return new TransactionSummaryPresenter(view,
                horizonApi,
                appFlagRepository,
                accountRepository,
                feesRepository,
                schedulerProvider);
    }
}
