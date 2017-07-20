package com.rakuten.tech.mobile.perf.core;

import java.util.Observable;


public class ObservableLocation extends Observable {
    public void updateValue(String location) {
        setChanged();
        notifyObservers(location);
    }
}
