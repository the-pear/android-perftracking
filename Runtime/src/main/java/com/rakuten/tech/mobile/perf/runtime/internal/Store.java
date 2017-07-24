package com.rakuten.tech.mobile.perf.runtime.internal;

import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.perf.core.CachingObservable;

/**
 * Store - used to created and return an observable with given type T.
 */
class Store<T> {
    @NonNull
    CachingObservable<T> observable;

    Store() {
        observable = new CachingObservable<>((T) new Object());
    }

    CachingObservable<T> getObservable() {
        return observable;
    }

}
