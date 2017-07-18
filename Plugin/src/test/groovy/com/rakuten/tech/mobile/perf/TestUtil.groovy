package com.rakuten.tech.mobile.perf

import org.gradle.api.logging.Logger

import static org.mockito.Mockito.mock

class TestUtil {
    static def resourceFile(name) {
        new File("src/test/resources/$name")
    }

    static Logger testLogger() {
        return mock(Logger)
    }
}