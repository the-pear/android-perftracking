package com.rakuten.tech.mobile.perf

import com.android.build.api.transform.*
import com.rakuten.tech.mobile.perf.rewriter.DummyReWriter
import com.rakuten.tech.mobile.perf.rewriter.PerformanceTrackingRewriter
import com.rakuten.tech.mobile.perf.rewriter.Rewriter
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class PerfTrackingTransform extends Transform {

    private final Project project
    private boolean enableRewrite;

    PerfTrackingTransform(Project project) {
        this.project = project
    }

    public void setEnableReWrite(boolean rewrite) {
        this.enableRewrite = rewrite
    }

    @Override
    String getName() {
        'PerfTracking'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return EnumSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return EnumSet.of(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(
            Context context,
            Collection<TransformInput> inputs,
            Collection<TransformInput> referencedInputs,
            TransformOutputProvider outputProvider,
            boolean isIncremental
    ) throws IOException, TransformException, InterruptedException {

        def input = []
        inputs.each {
            [it.jarInputs, it.directoryInputs]*.each { input << "$it.file" }
        }
        Rewriter rewriter;
        Logger log;
        if (enableRewrite) {
            log = Logging.getLogger(PerformanceTrackingRewriter.class.getName());
            rewriter = new PerformanceTrackingRewriter(log);
        } else {
            log = Logging.getLogger(DummyReWriter.class.getName());
            rewriter = new DummyReWriter(log);
        }


        rewriter.input = input.join(File.pathSeparator)
        rewriter.outputJar = outputProvider.getContentLocation("classes", outputTypes, scopes, Format.JAR).toString()
        rewriter.tempJar = "${context.temporaryDir}${File.separator}classes.jar"
        rewriter.classpath = project.android.bootClasspath.join(File.pathSeparator)
        rewriter.compileSdkVersion = project.android.compileSdkVersion

        log.debug("INPUT:  $rewriter.input")
        log.debug("OUTPUT:  $rewriter.outputJar")
        log.debug("TMP JAR:  $rewriter.tempJar")
        log.debug("PATH:  $rewriter.classpath")

        rewriter.rewrite();

    }
}