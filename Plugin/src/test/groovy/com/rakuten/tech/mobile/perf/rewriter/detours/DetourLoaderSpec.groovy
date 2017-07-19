package com.rakuten.tech.mobile.perf.rewriter.detours

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

import static com.rakuten.tech.mobile.perf.TestUtil.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class DetourLoaderSpec {
    DetourLoader detourLoader

    @Before void setUp() {
        detourLoader = new DetourLoader(testLogger())
    }

    @Test void "should return a list of detours for the input classNode, if any detour annotations exists, input classnode contains calldetour"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        ClassNode classNode = jar.getClassNode("${detoursPkg}.URLDetours")

        ArrayList<Detourer> detourers = detourLoader.load(classNode)

        assert detourers.get(0) instanceof CallDetour
    }

    @Test void "should return a empty detours list if the class node does not contain any detour annotations"() {
        ClassJar jar = new ClassJar(resourceFile("user-testUI.jar"))
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")

        ArrayList<Detourer> detourers = detourLoader.load(classNode)

        assert detourers.size() == 0
    }

    @Test void "should return a list of detours for the input classNode, if any detour annotations exists, input classnode contains parameterDetour"() {

        ClassNode classNodeStub = mock(ClassNode)
        classNodeStub.methods = [createMethodNode("testName", "(Ljava/lang/String;)V", [createAnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/DetourConstructorParameter;")], null)]

        ArrayList<Detourer> detourers = detourLoader.load(classNodeStub)

        assert detourers.get(0) instanceof ParameterDetour
    }

    @Test void "should return a list of detours for the input classNode, if any detour annotations exists, input classnode contains staticCallDetour"() {
        ClassNode classNodeStub = mock(ClassNode)
        classNodeStub.methods = [createMethodNode("testName", null, [createAnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/DetourStaticCall;")], createInstructionList("testName"))]

        ArrayList<Detourer> detourers = detourLoader.load(classNodeStub)

        assert detourers.get(0) instanceof StaticCallDetour

    }

    @Test void "should return a empty detours list if the methodNode name is not equal to the methodInstanceNode name "() {
        ClassNode classNodeStub = mock(ClassNode)
        classNodeStub.methods = [createMethodNode("testName", null, [createAnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/DetourStaticCall;")], createInstructionList("testName1"))]

        ArrayList<Detourer> detourers = detourLoader.load(classNodeStub)

        assert detourers.size() == 0
    }

    private InsnList createInstructionList(def methodInstNodeName) {
        InsnList insnListStub = mock(InsnList)
        when(insnListStub.size()).thenReturn(1)
        MethodInsnNode methodInsnNodeStub = mock(MethodInsnNode)
        methodInsnNodeStub.name = methodInstNodeName
        when(insnListStub.get(0)).thenReturn(methodInsnNodeStub)
        return insnListStub
    }

    private AnnotationNode createAnnotationNode(def annotationName) {
        AnnotationNode annotationNodeStub = mock(AnnotationNode)
        annotationNodeStub.desc = annotationName
        Type typeStub =  mock(Type)
        when(typeStub.getClassName()).thenReturn("java.lang.Object")
        ArrayList<Object> objects = new ArrayList<Object>()
        objects.add("value")
        objects.add(typeStub)
        annotationNodeStub.values = objects
        return annotationNodeStub
    }

    private MethodNode createMethodNode(def methodName, def methodDesc, def annotations, def instructions) {
        MethodNode methodNodeStub = mock(MethodNode)
        methodNodeStub.name = methodName
        methodNodeStub.desc = methodDesc
        methodNodeStub.visibleAnnotations = annotations
        methodNodeStub.instructions = instructions
        return methodNodeStub
    }
}