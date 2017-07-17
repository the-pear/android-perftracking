package com.rakuten.tech.mobile.perf.rewriter.detours

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static org.mockito.Mockito.mock

public class DetourerSpec{
    Detourer detourer

    @Before void setUp() {
        ClassProvider classProviderMock = mock(ClassProvider.class)
        detourer = new Detourer(classProviderMock)
    }

    @Test void "test"() {
        Detour detourStub = mock(Detour.class)
        detourStub.matchMethod = "matchMethod"
        detourStub.matchDesc = "matchDesc"

        detourer.add(detourStub)
    }

    @Test void "test1"() {
        Detour detourStub = mock(Detour.class)
        detourStub.matchMethod = "matchMethod"
        detourStub.matchDesc = "matchDesc"

        detourer.add(detourStub)
        detourer.add(detourStub)
    }

    /*@Test void "test2"() {
        DetourLoader detourLoader = new DetourLoader(Logging.getLogger("test"))
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.detours.URLDetours")
        ArrayList<Detourer> detourers = detourLoader.load(classNode)
        ClassProvider classProvider = new ClassProvider(resourceFile("user-testUI.jar").absolutePath)
        Detourer detourer1 = Detourer(classProvider)
        detourer1.add(detourers.get(0))
        ClassWriter cw = new ClassWriter(classProvider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = cw;
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.detours.URLDetours")
        ClassVisitor classVisitor = detourer1.rewrite(Object.class, visitor)
        assert classVisitor != visitor
        //reader.accept(classVisitor, 0)
    }*/
}