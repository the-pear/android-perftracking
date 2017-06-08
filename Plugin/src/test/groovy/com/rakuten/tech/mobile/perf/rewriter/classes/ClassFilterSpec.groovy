package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Test

public class ClassFilterSpec {

    ClassFilter classFilter

    @Before
    def void setup() {
        classFilter = new ClassFilter()
    }

    @Test
    def void "should exclude all classes from package, but still accept classes from other packages"() {
        classFilter.exclude("package.to.exclude")
        assert classFilter.canRewrite("package.to.exclude.SomeClass") == false
        assert classFilter.canRewrite("other.package.OtherClass") == true
    }

    @Test
    def void "should accept all classes when we exclude null packages"() {
        classFilter.exclude(null)
        assert classFilter.get_exclude().size() == 0
        assert classFilter.canRewrite("package.any.nonexclude.MyClass") == true
    }

    @Test
    def void "should exclude classes from all excluded packages"() {
        classFilter.exclude(null + File.pathSeparator + "package.to.exclude1" + File.pathSeparator + "package.to.exclude2")
        assert classFilter.get_exclude().size() == 3
        assert classFilter.canRewrite("package.any.nonexclude.SisClass") == true
        assert classFilter.canRewrite("package.to.exclude1.FathClass") == false
        assert classFilter.canRewrite("package.to.exclude2.BroClass") == false
        assert classFilter.canRewrite("null.MothClass") == false
    }
}