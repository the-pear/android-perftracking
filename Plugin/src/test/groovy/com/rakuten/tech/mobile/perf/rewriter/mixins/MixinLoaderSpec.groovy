package com.rakuten.tech.mobile.perf.rewriter.mixins

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar
import org.gradle.api.logging.Logging
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

import static com.rakuten.tech.mobile.perf.TestUtil.resourceFile

public class MixinLoaderSpec {
    MixinLoader mixinLoader
    ClassJar jar
    ClassNode classNode

    @Before def void setup() {
        mixinLoader = new MixinLoader(Logging.getLogger(MixinLoaderSpec.simpleName))
        jar = new ClassJar(resourceFile("user-TestUI.jar"))
        classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.VolleyHurlStackMixin")
    }

    @Test def void "should create Mixin Object for classNode with MixSubclassOf annotation"() {
        classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.ActivityMixin")

        String subClassName = mixinLoader.loadMixin(classNode).targetSubclassOf

        assert subClassName != null
    }

    @Test def void "should create Mixin Object for classNode with MixImplementationOf annotation"() {
        classNode = jar.getClassNode("com.rakuten.tech.mobile.perf.core.mixins.AdapterViewOnItemClickListenerMixin")

        String targetImplementationName = mixinLoader.loadMixin(classNode).targetImplementationOf

        assert targetImplementationName != null
    }

    @Test def void "should create Mixin Object for classNode with MixClass annotation"() {
        String className = mixinLoader.loadMixin(classNode).mixinClass

        assert className != null
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains visible annotations, but exclude null AnnotationNode"() {
        def fieldNodeList = []
        FieldNode fieldNode = new FieldNode(0, "testFieldName", null, null, new Integer(1))
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = new FieldNode(0, null, null, null, new Integer(1))
        fieldNodeList.add(fieldNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;")
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList
        classNode.fields = fieldNodeList

        String name = mixinLoader.loadMixin(classNode).fields.get(0).name

        assert name == "testFieldName"
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains invisible annotation, but exclude null AnnotationNode"() {
        def fieldNodeList = []
        FieldNode fieldNode = new FieldNode(0, "testFieldName", null, null, new Integer(1))
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = new FieldNode(0, null, null, null, new Integer(1))
        fieldNodeList.add(fieldNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;")
        annotationNodeList.add(annotationNode)
        fieldNode.invisibleAnnotations = annotationNodeList
        classNode.fields = fieldNodeList

        String name = mixinLoader.loadMixin(classNode).fields.get(0).name

        assert name == "testFieldName"
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains visible annotations, but exclude empty AnnotationNodeList"() {
        def fieldNodeList = []
        FieldNode fieldNode = new FieldNode(0, "testFieldName", null, null, new Integer(1))
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = new FieldNode(0, null, null, null, new Integer(1))
        fieldNodeList.add(fieldNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;")
        annotationNodeList.add(annotationNode)
        fieldNode.visibleAnnotations = annotationNodeList
        def annotationNodeList2 = []
        fieldNode2.visibleAnnotations = annotationNodeList2
        classNode.fields = fieldNodeList

        String name = mixinLoader.loadMixin(classNode).fields.get(0).name

        assert name == "testFieldName"
    }

    @Test def void "should create a mixin object with fields if the ClassNode contains invisible annotation, but exclude empty AnnotationNodeList"() {
        def fieldNodeList = []
        FieldNode fieldNode = new FieldNode(0, "testFieldName", null, null, new Integer(1))
        fieldNodeList.add(fieldNode)
        FieldNode fieldNode2 = new FieldNode(0, null, null, null, new Integer(1))
        fieldNodeList.add(fieldNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/AddField;")
        annotationNodeList.add(annotationNode)
        fieldNode.invisibleAnnotations = annotationNodeList
        def annotationNodeList2 = []
        fieldNode2.invisibleAnnotations = annotationNodeList2
        classNode.fields = fieldNodeList

        String name = mixinLoader.loadMixin(classNode).fields.get(0).name

        assert name == "testFieldName"
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains visible annotations, but exclude null AnnotationNode"() {
        def methodNodeList = []
        MethodNode methodNode = new MethodNode(0, "testFieldName", null, null, new String[0])
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = new MethodNode(0, null, null, null, new String[0])
        methodNodeList.add(methodNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;")
        annotationNodeList.add(annotationNode)
        methodNode.visibleAnnotations = annotationNodeList
        classNode.methods = methodNodeList

        int size = mixinLoader.loadMixin(classNode).methods.size()

        assert size == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains invisible annotation, but exclude null AnnotationNode"() {
        def methodNodeList = []
        MethodNode methodNode = new MethodNode(0, "testFieldName", null, null, new String[0])
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = new MethodNode(0, null, null, null, new String[0])
        methodNodeList.add(methodNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;")
        annotationNodeList.add(annotationNode)
        methodNode.invisibleAnnotations = annotationNodeList
        classNode.methods = methodNodeList

        int size = mixinLoader.loadMixin(classNode).methods.size()

        assert size == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains visible annotations, but exclude empty AnnotationNodeList"() {
        def methodNodeList = []
        MethodNode methodNode = new MethodNode(0, "testFieldName", null, null, new String[0])
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = new MethodNode(0, null, null, null, new String[0])
        methodNodeList.add(methodNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;")
        annotationNodeList.add(annotationNode)
        methodNode.visibleAnnotations = annotationNodeList
        def annotationNodeList2 = []
        methodNode2.visibleAnnotations = annotationNodeList2
        classNode.methods = methodNodeList

        int size = mixinLoader.loadMixin(classNode).methods.size()

        assert size == 1
    }

    @Test def void "should create a mixin object with methods if the ClassNode contains invisible annotation, but exclude empty AnnotationNodeList"() {
        def methodNodeList = []
        MethodNode methodNode = new MethodNode(0, "testFieldName", null, null, new String[0])
        methodNodeList.add(methodNode)
        MethodNode methodNode2 = new MethodNode(0, null, null, null, new String[0])
        methodNodeList.add(methodNode2)
        def annotationNodeList = []
        AnnotationNode annotationNode = new AnnotationNode("Lcom/rakuten/tech/mobile/perf/core/annotations/ReplaceMethod;")
        annotationNodeList.add(annotationNode)
        methodNode.invisibleAnnotations = annotationNodeList
        def annotationNodeList2 = []
        methodNode2.invisibleAnnotations = annotationNodeList2
        classNode.methods = methodNodeList

        int size = mixinLoader.loadMixin(classNode).methods.size()

        assert size == 1
    }
}