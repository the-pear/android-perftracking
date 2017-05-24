package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Test

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile;

class ClassProviderSpec {
    final String existingClass = "com.rakuten.tech.mobile.perf.core.Sender"
    ClassProvider provider

    @Before void setup() {
        provider = new ClassProvider(resourceFile("user-TestUI.jar").absolutePath)
    }

    @Test void "should provide existing class"() {
        assert provider.getClass(existingClass) != null
    }

    @Test(expected = RuntimeException.class)
    void "should fail for non-existing class"() {
        provider.getClass("some.made.up.Clazz")
    }

}