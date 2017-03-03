package jp.co.rakuten.sdtd.perf

import com.android.build.api.transform.*
import jp.co.rakuten.sdtd.perf.rewriter.Log
import jp.co.rakuten.sdtd.perf.rewriter.Rewriter
import org.gradle.api.Project

class PerfTrackingTransform extends Transform {

    private final Project project

    PerfTrackingTransform(Project project) {
        this.project = project
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
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        def input = []
        inputs.each {
            it.jarInputs.each {
                input << it.file.toString()
            }
            it.directoryInputs.each {
                input << it.file.toString()
            }
        }

        Rewriter rewriter = new Rewriter()
        rewriter.input = input.join(';')
        rewriter.outputJar = outputProvider.getContentLocation("classes", outputTypes, scopes, Format.JAR).toString()
        rewriter.tempJar = context.temporaryDir.toString() + '\\classes.jar'
        rewriter.classpath = project.android.bootClasspath.toList()
        rewriter.log.level = Log.INFO

        println rewriter.input
        println rewriter.outputJar
        println rewriter.tempJar
        println rewriter.classpath

        rewriter.rewrite()
    }
}
