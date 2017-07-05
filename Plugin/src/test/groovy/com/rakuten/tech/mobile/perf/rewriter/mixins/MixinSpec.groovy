package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider
import org.gradle.internal.impldep.org.junit.Ignore
import org.objectweb.asm.tree.AnnotationNode
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

class MixinSpec {

    Mixin mixin

    @Before def void setup() {
        mixin = new Mixin(Logging.getLogger(MixinSpec.simpleName))
    }

    @Test def void "should return false if the input class does not belong to com.rakuten.tech.mobile.perf.core.mixins "() {
        assert !mixin.match(Object.class)
    }

    @RunWith(Parameterized)
    static class MixinMatcherPositiveSpec {
        private String mixinInput
        private String classInput

        @Parameters
        static Collection<Object[]> data() {

            def data = [["com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin", "com.rakuten.tech.mobile.perf.core.mixins.TestTargetImplementationOf"],
                        ["com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin", "com.rakuten.tech.mobile.perf.core.mixins.TestTargetSubClassOf"],
                        ["com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin", "com.android.volley.toolbox.HurlStack"]]
            return data*.toArray()
        }

        public MixinMatcherPositiveSpec(final String mixinInput, final String classInput) {
            this.mixinInput = mixinInput
            this.classInput = classInput
        }

        @Test
        void "should match the input mixin object with the class input as mixin objects conditions are satisfied"() {
            ClassJar jar = new ClassJar(resourceFile("user-TestUI.jar"))
            ClassProvider provider = new ClassProvider(resourceFile("Core.jar").absolutePath)
            ClassNode classNode = jar.getClassNode(mixinInput)
            MixinLoader mixinLoader = new MixinLoader(Logging.getLogger(MixinLoaderSpec.simpleName))
            Mixin mixinTest = mixinLoader.loadMixin(classNode)
            assert mixinTest.match(provider.getClass(classInput))
        }
    }

    @RunWith(Parameterized)
    static class MixinMatcherNegativeSpec {
        private String mixinInput
        private String classInput

        @Parameters
        static Collection<Object[]> data() {

            def data = [["com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin", "java.lang.Thread"],
                        ["com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin", "android.webkit.WebChromeClient"],
                        ["com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin", "android.app.Activity"]]
            return data*.toArray()
        }

        public MixinMatcherNegativeSpec(final String mixinInput, final String classInput) {
            this.mixinInput = mixinInput
            this.classInput = classInput
        }

        @Test
        void "should not match the input mixin object with the class input as mixin objects conditions are not satisfied"() {
            ClassJar jar = new ClassJar(resourceFile("user-TestUI.jar"))
            ClassProvider provider = new ClassProvider(resourceFile("Core.jar").absolutePath)
            ClassNode classNode = jar.getClassNode(mixinInput)
            MixinLoader mixinLoader = new MixinLoader(Logging.getLogger(MixinLoaderSpec.simpleName))
            Mixin mixinTest = mixinLoader.loadMixin(classNode)
            assert !mixinTest.match(provider.getClass(classInput))
        }
    }

}