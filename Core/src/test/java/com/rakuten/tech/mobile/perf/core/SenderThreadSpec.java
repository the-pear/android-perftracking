package com.rakuten.tech.mobile.perf.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SenderThreadSpec {

    @Mock
    Sender sender;
    SenderThread senderThread;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        senderThread = new SenderThread(sender);
        senderThread.start();
    }

    @Test
    public void shouldStopSenderThread() throws InterruptedException {
        assertTrue(senderThread.isAlive());
        senderThread.stopRunning();
        assertFalse(senderThread.isRunning());
        senderThread.interrupt();
        Thread.sleep(10);
        assertFalse(senderThread.isAlive());
    }
}