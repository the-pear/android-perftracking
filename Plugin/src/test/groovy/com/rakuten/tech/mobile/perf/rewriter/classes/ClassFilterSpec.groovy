package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Rule
import org.junit.Test

public class ClassFilterSpec {

    ClassFilter classFilter

    @Before def void setup() {
        classFilter = new ClassFilter()
    }

    @Test def void "canReWrite should return false when given name present in exclude list"() {
        classFilter.exclude("test")
        assert classFilter.canRewrite("test.") == false
    }

    @Test def void "canReWrite should return true when given name not present in exclude list"() {
        classFilter.exclude("test")
        assert classFilter.canRewrite("test") == true
    }

    @Test def void "canReWrite should return true with empty exclude list"() {
        classFilter.exclude(null)
        assert classFilter.canRewrite("test") == true
    }
}