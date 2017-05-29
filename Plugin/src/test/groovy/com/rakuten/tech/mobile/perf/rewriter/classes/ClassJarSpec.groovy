package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Test
import static com.rakuten.tech.mobile.perf.TestUtil.*

class ClassJarSpec {
    ClassJar jar

    final String existingClass = "com.rakuten.tech.mobile.perf.core.Sender"

    @Before void setup() {
        jar = new ClassJar(resourceFile("user-TestUI.jar"))
    }

    @Test void "should read jar content"() {
        assert jar.getJarFile() != null
        assert jar.getClasses() != null
        assert jar.getClasses().size() > 0
    }

    @Test void "should lookup class in jar"() {
        assert !jar.hasClass("some.made.up.Clazz")
        assert jar.hasClass(existingClass)
    }

    @Test void "should provide ClassReader"() {
        assert jar.getClassReader(existingClass) != null
    }

    @Test void "should provide ClassNode"() {
        assert jar.getClassNode(existingClass) != null
    }

    @Test (expected = RuntimeException)
    void "should throw runtime exception on null constructor Paramater"() {
        ClassJar jar = new ClassJar(null)
    }

    @Test (expected = RuntimeException)
    void "should throw runtime exception on null Paramater"() {
        jar.getClassReader(null)
    }
}