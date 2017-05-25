package com.rakuten.tech.mobile.perf.rewriter.classes

import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

public class ClassWriterSpec {

    ClassWriter classWriter

    @Before
    def void setup() {
        classWriter = new ClassWriter(new ClassProvider(resourceFile("user-TestUI.jar").absolutePath), ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
    }

    @Test
    def void "call to getCommonSuperClass should return super type"() {
        String type1 = "jp.co.rakuten.api.rae.engine.TokenRequest"
        String type2 = "jp.co.rakuten.api.rae.engine.TokenCancelRequest"
        assert classWriter.getCommonSuperClass(type1, type2) == "jp/co/rakuten/api/rae/engine/EngineBaseRequest"
    }

    @Test
    def void "call to getCommonSuperClass should return Object type"() {
        String type1 = "jp.co.rakuten.api.rae.engine.TokenRequest"
        String type2 = "jp.co.rakuten.api.core.TokenableRequest"
        assert classWriter.getCommonSuperClass(type1, type2) == "java/lang/Object"
    }

    @Test(expected = RuntimeException.class)
    def void "call to getCommonSuperClass should throw RuntimeException"() {
        String type1 = "test"
        String type2 = "test"
        classWriter.getCommonSuperClass(type1, type2)
    }

    @Test
    def void "call to getCommonSuperClass should return assignable super"() {
        String type1 = "jp.co.rakuten.api.rae.engine.TokenRequest"
        String type2 = "jp.co.rakuten.api.rae.engine.EngineBaseRequest"
        assert classWriter.getCommonSuperClass(type1, type2) == "jp.co.rakuten.api.rae.engine.EngineBaseRequest"
    }

    @Test
    def void "call to getCommonSuperClass should return assignable super vice versa"() {
        String type1 = "jp.co.rakuten.api.rae.engine.EngineBaseRequest"
        String type2 = "jp.co.rakuten.api.rae.engine.TokenRequest"
        assert classWriter.getCommonSuperClass(type1, type2) == "jp.co.rakuten.api.rae.engine.EngineBaseRequest"
    }

}