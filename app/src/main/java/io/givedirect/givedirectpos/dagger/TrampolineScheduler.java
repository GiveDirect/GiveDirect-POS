package io.givedirect.givedirectpos.dagger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

/**
 * An {@link ObservableTransformer} that modifies the source observable to subscribe and observe on
 * the current thread. Useful for unit testing.
 * @param <T>
 */
public class TrampolineScheduler<T> implements ObservableTransformer<T, T> {
    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline());
    }
}
