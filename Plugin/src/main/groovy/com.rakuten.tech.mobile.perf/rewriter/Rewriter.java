package com.rakuten.tech.mobile.perf.rewriter;

import java.io.File;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.rakuten.tech.mobile.perf.rewriter.classes.ClassFilter;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJarMaker;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassTrimmer;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassWriter;
import com.rakuten.tech.mobile.perf.rewriter.detours.Detour;
import com.rakuten.tech.mobile.perf.rewriter.detours.DetourLoader;
import com.rakuten.tech.mobile.perf.rewriter.detours.Detourer;
import com.rakuten.tech.mobile.perf.rewriter.mixins.Mixer;
import com.rakuten.tech.mobile.perf.rewriter.mixins.MixinLoader;

public class Rewriter {

    public String input;
    public String outputJar;
    public String tempJar;
    public String classpath;
    public String exclude;
    public String compileSdkVersion;
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
        ClassTrimmer trimmer = new ClassTrimmer(compileSdkVersion, provider, log);

        DetourLoader detourLoader = new DetourLoader(log);
        Detourer detourer = new Detourer();

        MixinLoader mixinLoader = new MixinLoader(log);
        Mixer mixer = new Mixer();

        for (String name : temp.getClasses()) {
            if (name.startsWith("com.rakuten.tech.mobile.core.detours.")) {
                log.debug("Found detours " + name);
                ClassNode cn = trimmer.trim(temp.getClassNode(name));
                if (cn != null) {
                    for (Detour detour : detourLoader.load(cn)) {
                        detourer.add(detour);
                    }
                }
            } else if (name.startsWith("com.rakuten.tech.mobile.core.mixins.")) {
                log.debug("Found mixin " + name);
                ClassNode cn = trimmer.trim(temp.getClassNode(name));
                if (cn != null) {
                    mixer.add(mixinLoader.loadMixin(cn));
                }
            }
        }

        ClassJarMaker outputMaker = new ClassJarMaker(new File(outputJar));
        try {
            ClassFilter filter = new ClassFilter();
            filter.exclude(exclude);

            for (String name : temp.getClasses()) {

                if (name.startsWith("com.tech.tech.mobile.core")) {
                    ClassNode cn = trimmer.trim(temp.getClassNode(name));
                    if (cn != null) {
                        ClassWriter cw = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                        cn.accept(cw);
                        outputMaker.add(name, cw.toByteArray());
                    }
                }
                else if (filter.canRewrite(name)) {
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
                }
                else {
                    log.debug("Adding class with no rewriting: " + name);
                    outputMaker.add(name, temp);
                }
            }
        } finally {
            outputMaker.Close();
        }
    }
}
