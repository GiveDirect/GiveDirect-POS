package io.givedirect.givedirectpos.presenter.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.givedirect.givedirectpos.model.persistence.transaction.Operation;
import io.givedirect.givedirectpos.model.repository.OperationPageDataSource.NetworkState;
import io.givedirect.givedirectpos.presenter.common.Presenter;

public interface TransactionsContract {
    interface TransactionsView {
        void observeData(@Nullable LiveData<PagedList<Operation>> oldData,
                         @NonNull LiveData<PagedList<Operation>> liveData,
                         @Nullable String accountId);

        void observeNetworkState(@Nullable LiveData<NetworkState> oldData,
                                 @NonNull LiveData<NetworkState> newData);

        void observeRefreshState(@Nullable LiveData<NetworkState> oldData,
                                 @NonNull LiveData<NetworkState> newData);
    }

    interface TransactionsPresenter extends Presenter {
        void onUserRefreshed();
    }
}
