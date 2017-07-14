package com.rakuten.tech.mobile.perf.core;

import java.util.Observable;


public class ObservableLocation extends Observable {
    private String location = "";

    public ObservableLocation(String location) {
        this.location = location;
    }

    public void setValue(String location) {
        this.location = location;
        setChanged();
        notifyObservers();
    }

    public String getValue() {
        return location;
    }
}
