package io.givedirect.givedirectpos.presenter.payment.accountsummary;

import dagger.Module;
import dagger.Provides;
import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.repository.AccountRepository;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.repository.FeesRepository;
import io.givedirect.givedirectpos.presenter.payment.accountsummary.AccountSummaryContract.AccountSummaryView;

@Module
public class AccountSummaryPresenterModule {
    @Provides
    public AccountSummaryContract.AccountSummaryPresenter provideAccountSummaryPresenter(AccountSummaryView view,
                                                                                         AccountRepository accountRepository,
                                                                                         AppFlagRepository appFlagRepository,
                                                                                         FeesRepository feesRepository,
                                                                                         SchedulerProvider schedulerProvider) {
        return new AccountSummaryPresenter(view,
                accountRepository,
                appFlagRepository,
                feesRepository,
                schedulerProvider);
    }
}
