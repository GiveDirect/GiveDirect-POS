package io.givedirect.givedirectpos.presenter.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import io.givedirect.givedirectpos.dagger.SchedulerProvider;
import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.persistence.transaction.Operation;
import io.givedirect.givedirectpos.model.repository.AppFlagRepository;
import io.givedirect.givedirectpos.model.repository.OperationPageDataSource;
import io.givedirect.givedirectpos.model.repository.OperationPageDataSource.NetworkState;
import io.givedirect.givedirectpos.presenter.common.BasePresenter;
import io.givedirect.givedirectpos.view.util.ViewUtils;
import okhttp3.OkHttpClient;

public class TransactionsPresenter extends BasePresenter<TransactionsContract.TransactionsView>
        implements TransactionsContract.TransactionsPresenter {
    private final static int PAGE_SIZE = 7;

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AppFlagRepository appFlagRepository;

    @NonNull
    private final OkHttpClient okHttpClient;

    @NonNull
    private final Moshi moshi;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private LiveData<PagedList<Operation>> liveData;

    @NonNull
    private MutableLiveData<OperationPageDataSource> source = new MutableLiveData<>();

    private boolean hasBoundData = false;

    @Inject
    TransactionsPresenter(@NonNull TransactionsContract.TransactionsView view,
                          @NonNull AppFlagRepository appFlagRepository,
                          @NonNull HorizonApi horizonApi,
                          @NonNull OkHttpClient okHttpClient,
                          @NonNull Moshi moshi,
                          @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.okHttpClient = okHttpClient;
        this.moshi = moshi;
        this.appFlagRepository = appFlagRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onUserRefreshed() {
        ViewUtils.whenNonNull(source.getValue(), DataSource::invalidate);
    }

    private void bindData() {
        appFlagRepository.getMerchantAddress()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(this::addDisposable)
                .subscribe(this::bindData);
    }

    private void bindData(@NonNull String accountId) {
        LiveData<PagedList<Operation>> newData = getData(accountId);
        view.observeData(liveData, newData, accountId);
        liveData = newData;
    }

    private LiveData<PagedList<Operation>> getData(@NonNull String accountId) {
        return new LivePagedListBuilder<>(new OperationsDataSourceFactory(accountId), PAGE_SIZE)
                .build();
    }

    @Override
    public void resume() {
        super.resume();
        if (!hasBoundData) {
            // Don't rebind data across resumes (may happen multiple times across the lifetime of
            // this presenter). Rebind for first resume or new instances.
            hasBoundData = true;
            bindData();
        }
    }

    private class OperationsDataSourceFactory extends DataSource.Factory<String, Operation> {
        @Nullable
        private final String accountId;

        OperationsDataSourceFactory(@Nullable String accountId) {
            this.accountId = accountId;
        }

        @Override
        public DataSource<String, Operation> create() {
            OperationPageDataSource dataSource =
                    new OperationPageDataSource(horizonApi, okHttpClient, moshi, accountId);
            OperationPageDataSource oldSource = source.getValue();

            LiveData<NetworkState> oldNetworkState = null;
            LiveData<NetworkState> oldRefreshState = null;
            if (oldSource != null) {
                oldNetworkState = oldSource.getNetworkState();
                oldRefreshState = oldSource.getInitialLoadState();
            }

            view.observeRefreshState(oldRefreshState, dataSource.getInitialLoadState());
            view.observeNetworkState(oldNetworkState, dataSource.getNetworkState());
            source.postValue(dataSource);
            return dataSource;
        }
    }
}
