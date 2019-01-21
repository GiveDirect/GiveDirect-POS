package io.givedirect.givedirectpos.dagger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Convenient way to inject a thread scheduler for asynchronous work.
 */
@Singleton
public class SchedulerProvider {
    @Inject
    public SchedulerProvider() {}

    public <T> ObservableTransformer<T, T> observableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public CompletableTransformer completableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> SingleTransformer<T, T> singleScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
