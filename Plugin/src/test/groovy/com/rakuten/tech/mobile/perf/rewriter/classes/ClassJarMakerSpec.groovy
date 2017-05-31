package com.rakuten.tech.mobile.perf.rewriter.classes

import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.rakuten.tech.mobile.perf.TestUtil.*

class ClassJarMakerSpec {

    @Rule
    public final TemporaryFolder projectDir = new TemporaryFolder(new File("tmp"))

    ClassJarMaker jar
    File jarTemp

    @Before
    void setup() {
        jarTemp = projectDir.newFile("test.jar")
        jar = new ClassJarMaker(jarTemp)
    }

    @Test
    void "should populate jar fie"() {
        jar.populate(resourceFile("user-TestUI.jar").absolutePath)
        jar.Close()
        ClassProvider classProvider = new ClassProvider(jarTemp.absolutePath)
        assert classProvider.getClass("com.rakuten.tech.mobile.perf.core.Sender") != null
    }

    @Test(expected = RuntimeException.class)
    void "should throw RuntimeException"() {
        ClassJar classJar = new ClassJar(resourceFile("user-TestUI.jar"))
        jar.populate(resourceFile("user-TestUI.jar").absolutePath)
        ArrayList<String> arrayList = classJar.getClasses()
        jar.add(arrayList.get(0), classJar)
    }

    @Test(expected = RuntimeException.class)
    void "should throw RuntimeException on null parameter for class initialization"() {
        new ClassJarMaker(null)
    }

    @Test(expected = RuntimeException.class)
    void "should throw RuntimeException on null parameter populate method"() {
        jar.populate(null)
    }

    @Test (expected = ClassFormatError.class)
    void "should add the class"() {
        jar.add("com.test.test", new byte[2])
        jar.Close()
        ClassProvider provider = new ClassProvider(jarTemp.absolutePath)
        provider.getClass("com.test.test").getResourceAsStream()
    }
}