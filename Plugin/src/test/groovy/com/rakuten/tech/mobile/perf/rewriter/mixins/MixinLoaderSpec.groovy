package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

public class MixinLoaderSpec {
    MixinLoader mixinLoader
    ClassJar jar

    @Before def void setup() {
        mixinLoader = new MixinLoader(Logging.getLogger(MixinLoaderSpec.simpleName))
        jar = new ClassJar(resourceFile("user-TestUI.jar"))
    }

    @Test def void "should create Mixin Object for classNode with MixSubclassOf annotation"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")
        assert mixinLoader.loadMixin(classNode).targetSubclassOf != null
    }

    @Test def void "should create Mixin Object for classNode with MixImplementationOf annotation"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")
        assert mixinLoader.loadMixin(classNode).targetImplementationOf != null
    }

    @Test def void "should create Mixin Object for classNode with MixClass annotation"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
        assert mixinLoader.loadMixin(classNode).mixinClass != null
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains visible annotations, but exclude null AnnotationNode"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = Mockito.mock(FieldNode.class)
        fieldNodeList.add(fieldNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;"
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList

        classNode.fields = fieldNodeList
        assert mixinLoader.loadMixin(classNode).fields.get(0).name.equals("testFieldName")
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains invisible annotation, but exclude null AnnotationNode"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = Mockito.mock(FieldNode.class)
        fieldNodeList.add(fieldNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;"
        annotationNodeList.add(annotationNode)
        fieldNode.invisibleAnnotations = annotationNodeList

        classNode.fields = fieldNodeList
        assert mixinLoader.loadMixin(classNode).fields.get(0).name.equals("testFieldName")
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains visible annotations, but exclude empty AnnotationNodeList"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = Mockito.mock(FieldNode.class)
        fieldNodeList.add(fieldNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;"
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList

        List<AnnotationNode> annotationNodeList2 = new ArrayList<AnnotationNode>()
        fieldNode2.visibleAnnotations = annotationNodeList2

        classNode.fields = fieldNodeList
        assert mixinLoader.loadMixin(classNode).fields.get(0).name.equals("testFieldName")
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains invisible annotation, but exclude empty AnnotationNodeList"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<FieldNode> fieldNodeList = new ArrayList<FieldNode>();
        FieldNode fieldNode = Mockito.mock(FieldNode.class)
        fieldNode.name = "testFieldName"
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = Mockito.mock(FieldNode.class)
        fieldNodeList.add(fieldNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;"
        annotationNodeList.add(annotationNode)
        fieldNode.invisibleAnnotations = annotationNodeList

        List<AnnotationNode> annotationNodeList2 = new ArrayList<AnnotationNode>()
        fieldNode2.invisibleAnnotations = annotationNodeList2

        classNode.fields = fieldNodeList
        assert mixinLoader.loadMixin(classNode).fields.get(0).name.equals("testFieldName")
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains visible annotations, but exclude null AnnotationNode"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<MethodNode> methodNodeList = new ArrayList<MethodNode>();
        MethodNode methodNode = Mockito.mock(MethodNode.class)
        methodNode.name = "testFieldName"
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = Mockito.mock(MethodNode.class)
        methodNodeList.add(methodNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;"
        annotationNodeList.add(annotationNode)
        methodNode.visibleAnnotations = annotationNodeList

        classNode.methods = methodNodeList
        assert mixinLoader.loadMixin(classNode).methods.size() == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains invisible annotation, but exclude null AnnotationNode"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<MethodNode> methodNodeList = new ArrayList<MethodNode>();
        MethodNode methodNode = Mockito.mock(MethodNode.class)
        methodNode.name = "testFieldName"
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = Mockito.mock(MethodNode.class)
        methodNodeList.add(methodNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;"
        annotationNodeList.add(annotationNode)
        methodNode.invisibleAnnotations = annotationNodeList

        classNode.methods = methodNodeList
        assert mixinLoader.loadMixin(classNode).methods.size() == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains visible annotations, but exclude empty AnnotationNodeList"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<MethodNode> methodNodeList = new ArrayList<MethodNode>();
        MethodNode methodNode = Mockito.mock(MethodNode.class)
        methodNode.name = "testFieldName"
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = Mockito.mock(MethodNode.class)
        methodNodeList.add(methodNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;"
        annotationNodeList.add(annotationNode)
        methodNode.visibleAnnotations = annotationNodeList

        List<AnnotationNode> annotationNodeList2 = new ArrayList<AnnotationNode>()
        methodNode2.visibleAnnotations = annotationNodeList2

        classNode.methods = methodNodeList
        assert mixinLoader.loadMixin(classNode).methods.size() == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains invisible annotation, but exclude empty AnnotationNodeList"() {
        ClassNode classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")

        List<MethodNode> methodNodeList = new ArrayList<MethodNode>();
        MethodNode methodNode = Mockito.mock(MethodNode.class)
        methodNode.name = "testFieldName"
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = Mockito.mock(MethodNode.class)
        methodNodeList.add(methodNode2)

        List<AnnotationNode> annotationNodeList = new ArrayList<AnnotationNode>()
        AnnotationNode annotationNode = Mockito.mock(AnnotationNode.class)
        annotationNode.desc = "Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;"
        annotationNodeList.add(annotationNode)
        methodNode.invisibleAnnotations = annotationNodeList

        List<AnnotationNode> annotationNodeList2 = new ArrayList<AnnotationNode>()
        methodNode2.invisibleAnnotations = annotationNodeList2

        classNode.methods = methodNodeList
        assert mixinLoader.loadMixin(classNode).methods.size() == 1
    }
}