package jp.co.rakuten.sdtd.perf.rewriter;

import java.io.File;

import org.objectweb.asm.ClassReader;

import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassFilter;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJar;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJarMaker;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassProvider;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassWriter;
import jp.co.rakuten.sdtd.perf.rewriter.detours.Detour;
import jp.co.rakuten.sdtd.perf.rewriter.detours.DetourLoader;
import jp.co.rakuten.sdtd.perf.rewriter.detours.Detourer;
import jp.co.rakuten.sdtd.perf.rewriter.mixins.Mixer;
import jp.co.rakuten.sdtd.perf.rewriter.mixins.MixinLoader;

public class Rewriter {

    public String input;
    public String outputJar;
    public String tempJar;
    public String classpath;
    public String exclude;
    public final Log log;

    public Rewriter() {
        log = new Log();
    }

    public void rewrite() {

        System.out.println(input);
        log.debug("Populating temp JAR");
        ClassJarMaker tempMaker = new ClassJarMaker(new File(tempJar));
        try {
            tempMaker.populate(input);
        } finally {
            tempMaker.Close();
        }

        ClassJar temp = new ClassJar(new File(tempJar));
        ClassProvider provider = new ClassProvider(classpath + File.pathSeparator + tempJar);

        DetourLoader detourLoader = new DetourLoader(log);
        Detourer detourer = new Detourer();

        MixinLoader mixinLoader = new MixinLoader(log);
        Mixer mixer = new Mixer();

        for (String name : temp.getClasses()) {
            if (name.startsWith("jp.co.rakuten.sdtd.perf.core.detours.")) {
                log.debug("Found detours " + name);
                for (Detour detour : detourLoader.load(temp.getClassNode(name))) {
                    detourer.add(detour);
                }
            } else if (name.startsWith("jp.co.rakuten.sdtd.perf.core.mixins.")) {
                log.debug("Found mixin " + name);
                mixer.add(mixinLoader.loadMixin(temp.getClassNode(name)));
            }
        }

        ClassJarMaker outputMaker = new ClassJarMaker(new File(outputJar));
        try {
            ClassFilter filter = new ClassFilter();
            filter.exclude("jp.co.rakuten.sdtd.perf.core");
            filter.exclude(exclude);

            for (String name : temp.getClasses()) {

                if (filter.canRewrite(name)) {
                    log.debug("Rewriting class: " + name);

                    try {
                        Class<?> clazz = provider.getClass(name);
                        ClassReader cr = temp.getClassReader(name);
                        ClassWriter cw = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                        cr.accept(detourer.getAdapter(clazz, provider, mixer.getAdapter(clazz, cw)), 0);
                        outputMaker.add(name, cw.toByteArray());

                    } catch (Throwable e) {
                        log.error("Failed to rewrite class: " + name, e);
                        outputMaker.add(name, temp);
                    }
                } else {
                    log.debug("Adding class with no rewriting: " + name);
                    outputMaker.add(name, temp);
                }
            }
        } finally {
            outputMaker.Close();
        }
    }
}
