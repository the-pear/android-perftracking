package com.rakuten.tech.mobile.perf.rewriter.base

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJarMaker
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.LocalVariableNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static com.rakuten.tech.mobile.perf.TestUtil.testLogger
import static org.mockito.Mockito.spy

class MaterializationSpec {
    @Rule public final TemporaryFolder tempDir = new TemporaryFolder()
    def index = 1
    File jar
    Materialization materialization

    Class clazz
    ClassWriter writer
    ClassReader reader
    Base base
    ClassProvider provider

    String targetClass = "jp.co.rakuten.sdtd.user.authenticator.AuthenticatorFederatedActivity"

    @Before void setup() {
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

        reader.accept(visitor, 0)

        assert materialization.internalSuperName == "android/accounts/AccountAuthenticatorActivity"
    }

    @Test void "should materialize and add the class to ClassJarMaker"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        Base baseStub = spy(new BaseLoader().loadBase(jar.getClassNode("com.rakuten.tech.mobile.perf.core.base.WebViewClientBase")))
        baseStub.internalName = "android/webkit/WebViewClient"
        materialization = new Materialization(baseStub, index++, provider, testLogger());
        List<LocalVariableNode> localVariables = baseStub.cn.methods.get(2).localVariables
        localVariables.get(2).signature = "Landroid/webkit/WebViewClient/testSignature"
        localVariables.get(1).signature = "testSignature"
        File tempJarFile = tempDir.newFile("temp.jar")
        ClassJarMaker classJarMaker = new ClassJarMaker(tempJarFile)

        materialization.materialize(classJarMaker)

        classJarMaker.Close()
        assert new ClassJar(tempJarFile).hasClass(materialization.name)
    }
}