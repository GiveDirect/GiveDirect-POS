package io.givedirect.givedirectpos.view.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.model.persistence.transaction.Operation;
import io.givedirect.givedirectpos.model.repository.OperationPageDataSource.NetworkState;
import io.givedirect.givedirectpos.presenter.transactions.TransactionsContract;
import io.givedirect.givedirectpos.view.common.BaseViewFragment;
import io.givedirect.givedirectpos.view.util.ViewUtils;

public class TransactionsFragment extends BaseViewFragment<TransactionsContract.TransactionsPresenter>
        implements TransactionsContract.TransactionsView {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private TransactionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transactions_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initRecyclerView();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorPrimaryLight);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.onUserRefreshed());
    }

    private void initRecyclerView() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new SlideUpAnimator());
        adapter = new TransactionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ViewUtils.addDividerDecoration(recyclerView, context, layoutManager.getOrientation());
    }

    @Override
    public void observeData(@Nullable LiveData<PagedList<Operation>> oldData,
                            @NonNull LiveData<PagedList<Operation>> liveData,
                            @Nullable String accountId) {
        removeObservers(oldData);
        adapter.setAccountId(accountId);
        liveData.observe(this, operations -> {
            adapter.submitList(operations);
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    recyclerView.scrollToPosition(0), 500);
        });
    }

    @Override
    public void observeNetworkState(@Nullable LiveData<NetworkState> oldData,
                                    @NonNull LiveData<NetworkState> newData) {
        removeObservers(oldData);
        newData.observe(this, networkState -> {
            adapter.setLoading(networkState == NetworkState.LOADING);
            if (networkState == NetworkState.FAILED) {
                onLoadFailed();
            }
        });
    }

    @Override
    public void observeRefreshState(@Nullable LiveData<NetworkState> oldData,
                                    @NonNull LiveData<NetworkState> newData) {
        removeObservers(oldData);
        newData.observe(this, networkState -> {
            swipeRefreshLayout.setRefreshing(networkState == NetworkState.LOADING);
            if (networkState == NetworkState.FAILED) {
                onLoadFailed();
            }
        });
    }

    private void onLoadFailed() {
        Toast.makeText(getContext(), R.string.failed_load_transactions,
                Toast.LENGTH_SHORT).show();
    }

    private void removeObservers(@Nullable LiveData oldData) {
        new Handler(Looper.getMainLooper()).post(() ->
                ViewUtils.whenNonNull(oldData, old ->
                        old.removeObservers(TransactionsFragment.this)));
    }

    public void refreshTransactions() {
        presenter.onUserRefreshed();
    }
}
