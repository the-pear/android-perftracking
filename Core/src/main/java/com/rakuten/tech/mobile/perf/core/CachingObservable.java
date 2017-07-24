package com.rakuten.tech.mobile.perf.core;


import java.util.Observable;

public class CachingObservable<T> extends Observable {

    private T value;

    public CachingObservable(T v) {
        value = v;
    }

    T getCachedValue() {
        return value;
    }

    public void publish(T newValue) {
        value = newValue;
        setChanged();
        notifyObservers(newValue);
    }
}
