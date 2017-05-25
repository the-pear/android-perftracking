package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Rule
import org.junit.Test

public class ClassFilterSpec {

    ClassFilter classFilter

    @Before def void setup() {
        classFilter = new ClassFilter()
    }

    @Test def void "add exclude file and test canReWrite should return false"() {
        classFilter.exclude("test")
        assert classFilter.canRewrite("test.") == false
    }

    @Test def void "add exclude file and test canReWrite should return true"() {
        classFilter.exclude("test")
        assert classFilter.canRewrite("test") == true
    }

    @Test def void "add null file exclude and canReWrite should return true"() {
        classFilter.exclude(null)
        assert classFilter.canRewrite("test") == true
    }
}