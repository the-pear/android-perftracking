package com.rakuten.tech.mobile.perf.runtime.shadow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import jp.co.rakuten.api.test.MockedQueue;

@Implements(RequestQueue.class)
public class RequestQueueShadow {
    public static MockedQueue queue = new MockedQueue();

    @Implementation public void start() { }

    @Implementation public <T> Request<T> add(Request<T> request) {
        return queue.add(request);
    }
}
