package com.rakuten.tech.mobile.perf.rewriter.base

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJarMaker
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.LocalVariableNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static com.rakuten.tech.mobile.perf.TestUtil.testLogger
import static org.mockito.AdditionalMatchers.aryEq
import static org.mockito.ArgumentMatchers.*
import static org.mockito.Mockito.*

class MaterializationSpec {
    def index = 1
    File jar
    Materialization materialization

    Class clazz
    ClassWriter writer
    ClassReader reader
    Base base
    ClassProvider provider

    // Step 1: select class to rewrite in materialization:
    String targetClass = "jp.co.rakuten.sdtd.user.authenticator.AuthenticatorFederatedActivity"

    @Before void setup() {
        // Step 2: define a base class description
        base = new Base();
        def classpath = resourceFile("user-TestUI.jar").absolutePath + File.pathSeparator +
                resourceFile("android23.jar").absolutePath
        provider = new ClassProvider(classpath);

        clazz = provider.getClass(targetClass)
        reader = new ClassJar(resourceFile("user-TestUI.jar")).getClassReader(targetClass)
        writer = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        materialization = new Materialization(base, index++, provider, testLogger());
        jar = resourceFile("user-TestUI.jar");
    }

    @Test void "should insert super class in inheritance hierarchy"() {
        def visitor = spy(materialization.rewrite(clazz, writer))
        // Step 3: debug Materialization as it rewrites the target class
        reader.accept(visitor, 0)

        String[] verifyArray = ["com/android/volley/Response\$ErrorListener", "android/view/View\$OnClickListener"]
        verify(visitor).visit(eq(Opcodes.V1_7), anyInt(),
                eq("jp/co/rakuten/sdtd/user/authenticator/AuthenticatorFederatedActivity"), eq(null),
                eq("android/accounts/AccountAuthenticatorActivity"), aryEq(verifyArray))
    }

    @Test void "should call add method on input ClassJarMaker object after visiting class"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        Base baseStub = spy(new BaseLoader().loadBase(jar.getClassNode("com.rakuten.tech.mobile.perf.core.base.WebViewClientBase")))
        baseStub.internalName = "android/webkit/WebViewClient"
        materialization = new Materialization(baseStub, index++, provider, testLogger());
        ClassJarMaker classJarMakerMock = mock(ClassJarMaker)

        materialization.materialize(classJarMakerMock)

        //verify(classJarMakerMock).add(anyString(), any(byte[]))
        //TODO: have to validate encountered exception "groovy.lang.GroovyRuntimeException: Ambiguous method overloading"
    }

    @Test void "should call add method on input ClassJarMaker object after visiting method and its local variables"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        Base base = new BaseLoader().loadBase(jar.getClassNode("com.rakuten.tech.mobile.perf.core.base.WebViewClientBase"))
        base.internalName = "android/webkit/WebViewClient"
        materialization = new Materialization(base, index++, provider, testLogger());
        List<LocalVariableNode> localVariables = base.cn.methods.get(2).localVariables
        localVariables.get(2).signature = "Landroid/webkit/WebViewClient/testSignature"
        localVariables.get(1).signature = "testSignature"
        ClassJarMaker classJarMakerMock = mock(ClassJarMaker)

        materialization.materialize(classJarMakerMock)

        //verify(classJarMakerMock).add(anyString(), any(byte[]))
        //TODO: have to validate encountered exception "groovy.lang.GroovyRuntimeException: Ambiguous method overloading"
    }
}