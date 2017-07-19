package com.rakuten.tech.mobile.perf.rewriter.detours

import org.junit.Before
import org.junit.Test
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import static com.rakuten.tech.mobile.perf.TestUtil.testLogger
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

public class CallDetourSpec {
    CallDetour callDetour

    @Before void setUp() {
        callDetour = new CallDetour(testLogger())
    }

    @Test void "should match if the input ownerClass is equal to the owner of the callDetour"() {
        callDetour.owner = "java.lang.Object"

        boolean match = callDetour.matchOwner(null, String.class)

        assert match
    }

    @Test void "should not match if the input ownerClass is not equal to the owner of the callDetour"() {
        callDetour.owner = "java.lang.class"

        boolean match = callDetour.matchOwner(null, String.class)

        assert !match
    }

    @Test void "should visit method instance for the callDetour inputs"() {
        callDetour.detourOwner = "java.lang.String"
        callDetour.detourDesc = "detourDesc"
        MethodVisitor methodVisitorMock = mock(MethodVisitor)

        callDetour.rewrite(methodVisitorMock, (int)0, "java.lang.Object", Object.class, "name", "desc", true)

        verify(methodVisitorMock).visitMethodInsn(eq(Opcodes.INVOKESTATIC), eq("java.lang.String"), eq("name"), eq("detourDesc"), eq(false))
    }
}