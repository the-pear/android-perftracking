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
    public void shouldInterruptSenderThread() {
        assertTrue(senderThread.isAlive());
        senderThread.setRunning(false);
        assertFalse(senderThread.isRunning());
        while (senderThread.isAlive());
        assertFalse(senderThread.isAlive());
    }
}