package com.rakuten.tech.mobile.perf.rewriter.detours

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.tree.ClassNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile
import static com.rakuten.tech.mobile.perf.TestUtil.testLogger
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

public class DetourerSpec{
    Detourer detourer

    @Before void setUp() {
        ClassProvider classProviderMock = mock(ClassProvider)
        detourer = new Detourer(classProviderMock)
    }

    @Test void "should add the input detour to the Detourer collection, should create an ArrayList and add the input into the collection"() {
        Detour detourStub = mock(Detour)
        detourStub.matchMethod = "matchMethod"
        detourStub.matchDesc = "matchDesc"

        detourer.add(detourStub)

        //TODO Validate (Maybe you need reflection.)
    }

    @Test void "should add the input detourers into the collection, should add new entry to the arraylist if matches"() {
        Detour detourStub = mock(Detour)
        detourStub.matchMethod = "matchMethod"
        detourStub.matchDesc = "matchDesc"

        detourer.add(detourStub)
        detourer.add(detourStub)

        //TODO Validate (Maybe you need reflection.)
    }

    @Test void "should visit the class and method with the data in collection"() {
        DetourLoader detourLoader = new DetourLoader(testLogger())
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.detours.URLDetours")
        ArrayList<Detourer> detourers = detourLoader.load(classNode)
        ClassProvider classProvider = new ClassProvider(resourceFile("user-testUI.jar").absolutePath)
        detourer.add(detourers.get(0))
        ClassVisitor visitor = new ClassWriter(classProvider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
        ClassReader reader = jar.getClassReader("com.rakuten.tech.mobile.perf.core.detours.URLDetours")
        ClassVisitor classVisitor = detourer.rewrite(Object.class, visitor)
        ClassVisitor classVisitorMock = spy(classVisitor)

        reader.accept(classVisitorMock, 0)

        verify(classVisitorMock).visitMethod(eq(9), eq("openConnection"), eq("(Ljava/net/URL;)Ljava/net/URLConnection;"), eq(null), any())
    }
}