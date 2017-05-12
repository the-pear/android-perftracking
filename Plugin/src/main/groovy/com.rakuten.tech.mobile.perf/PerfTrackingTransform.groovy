package com.rakuten.tech.mobile.perf

import com.android.build.api.transform.*
import com.rakuten.tech.mobile.perf.rewriter.DummyReWriter
import com.rakuten.tech.mobile.perf.rewriter.Rewriter
import com.rakuten.tech.mobile.perf.rewriter.RewriterStrategy
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class PerfTrackingTransform extends Transform {

    private final Project project
    private boolean mEnableReWrite;
    private RewriterStrategy mRewriterStrategy;

    PerfTrackingTransform(Project project, boolean enableReWrite) {
        this.project = project
        this.mEnableReWrite = enableReWrite
    }

    public void setEnableReWrite(boolean enableReWrite) {
        this.mEnableReWrite = enableReWrite
    }

    public boolean getEnableReWrite() {
        this.mEnableReWrite
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

        Logger log;
        if (mEnableReWrite) {
            log = Logging.getLogger(Rewriter.class.getName());
            mRewriterStrategy = new Rewriter(log);
        } else {
            log = Logging.getLogger(DummyReWriter.class.getName());
            mRewriterStrategy = new DummyReWriter(log);
        }


        mRewriterStrategy.input = input.join(File.pathSeparator)
        mRewriterStrategy.outputJar = outputProvider.getContentLocation("classes", outputTypes, scopes, Format.JAR).toString()
        mRewriterStrategy.tempJar = "${context.temporaryDir}${File.separator}classes.jar"
        mRewriterStrategy.classpath = project.android.bootClasspath.join(File.pathSeparator)
        mRewriterStrategy.compileSdkVersion = project.android.compileSdkVersion

        log.debug("INPUT:  $mRewriterStrategy.input")
        log.debug("OUTPUT:  $mRewriterStrategy.outputJar")
        log.debug("TMP JAR:  $mRewriterStrategy.tempJar")
        log.debug("PATH:  $mRewriterStrategy.classpath")

        mRewriterStrategy.rewrite();

    }
}