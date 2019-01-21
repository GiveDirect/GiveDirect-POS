package io.givedirect.givedirectpos.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.givedirect.givedirectpos.model.api.HorizonApi;
import io.givedirect.givedirectpos.model.persistence.transaction.Operation;
import io.givedirect.givedirectpos.model.persistence.transaction.OperationPage;
import io.givedirect.givedirectpos.view.util.TextUtils;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class OperationPageDataSource extends PageKeyedDataSource<String, Operation> {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final OkHttpClient okHttpClient;

    @Nullable
    private final String accountId;

    @NonNull
    private final JsonAdapter<OperationPage> operationPageAdapter;

    @NonNull
    private final MutableLiveData<NetworkState> networkState = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<NetworkState> initialLoadState = new MutableLiveData<>();

    public OperationPageDataSource(@NonNull HorizonApi horizonApi,
                                   @NonNull OkHttpClient okHttpClient,
                                   @NonNull Moshi moshi,
                                   @Nullable String accountId) {
        this.horizonApi = horizonApi;
        this.okHttpClient = okHttpClient;
        this.accountId = accountId;
        operationPageAdapter = moshi.adapter(OperationPage.class);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params,
                            @NonNull LoadInitialCallback<String, Operation> callback) {
        if (accountId == null) {
            callback.onResult(new ArrayList<>(), null, null);
            return;
        }

        initialLoadState.postValue(NetworkState.LOADING);
        try {
            AtomicReference<String> nextPage = new AtomicReference<>();
            AtomicReference<String> prevPage = new AtomicReference<>();
            List<Operation> operationList =
                    horizonApi.getFirstOperationsPage(accountId, params.requestedLoadSize)
                            .toObservable()
                            .doOnNext(operationPage -> {
                                nextPage.set(operationPage.getNextPageLink());
                                prevPage.set(operationPage.getPreviousPageLink());
                            })
                            .map(OperationPage::getOperations)
                            .flatMapIterable(operations -> operations)
                            .flatMap(this::populateOperationTransaction)
                            .toList()
                            .blockingGet();

            callback.onResult(operationList, prevPage.get(), nextPage.get());
            initialLoadState.postValue(NetworkState.SUCCESS);
        } catch (Exception e) {
            Timber.e(e, "Failed to load initial transactions");
            initialLoadState.postValue(NetworkState.FAILED);
        }
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoadState() {
        return initialLoadState;
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params,
                           @NonNull LoadCallback<String, Operation> callback) {
        // Ignored, since we only ever append to our initial load.
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params,
                          @NonNull LoadCallback<String, Operation> callback) {
        if (accountId == null) {
            callback.onResult(new ArrayList<>(), null);
            return;
        }

        networkState.postValue(NetworkState.LOADING);
        Request request = new Request.Builder().url(params.key).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            OperationPage operationPage = operationPageAdapter.fromJson(response.body().source());

            List<Operation> operations = operationPage.getOperations();
            if (operations.isEmpty()) {
                callback.onResult(operations, null);
                networkState.postValue(NetworkState.SUCCESS);
                return;
            }

            List<Operation> filledOperations = Observable.fromIterable(operations)
                    .flatMap(this::populateOperationTransaction)
                    .toList()
                    .blockingGet();

            callback.onResult(filledOperations, operationPage.getNextPageLink());
            networkState.postValue(NetworkState.SUCCESS);
        } catch (Exception e) {
            Timber.e(e, "Failed to load more operations");
            networkState.postValue(NetworkState.FAILED);
        }
    }

    private Observable<Operation> populateOperationTransaction(@NonNull Operation operation) {
        String transactionHash = operation.getTransaction_hash();
        if (!TextUtils.isEmpty(transactionHash)) {
            return horizonApi.getTransaction(operation.getTransaction_hash())
                    .map(transaction -> {
                        operation.setTransaction(transaction);
                        return operation;
                    })
                    .toObservable();
        } else {
            return Observable.just(operation);
        }
    }

    public enum NetworkState {
        LOADING,
        SUCCESS,
        FAILED
    }
}
