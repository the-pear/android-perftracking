package com.rakuten.tech.mobile.perf.rewriter;

import com.rakuten.tech.mobile.perf.rewriter.base.Rebaser;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassFilter;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJar;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassJarMaker;
import com.rakuten.tech.mobile.perf.rewriter.classes.ClassProvider;

import org.gradle.api.logging.Logger;

import java.io.File;

/**
 * Dummy ReWriter to exclude performance tracking from debug build.
 */

public class DummyReWriter implements RewriterStrategy {

    public String input;
    public String outputJar;
    public String tempJar;
    public String classpath;
    public String exclude;
    public String compileSdkVersion;
    public final Logger _log;

    public DummyReWriter(Logger log) {
        _log = log;
    }

    public void rewrite() {
        _log.debug(input);
        _log.debug("DummyReWriter Populating temp JAR");
        System.out.println("DummyReWriter Populating temp JAR");
        ClassJarMaker tempMaker = new ClassJarMaker(new File(tempJar));
        try {
            tempMaker.populate(input);
        } finally {
            tempMaker.Close();
        }

        ClassJar temp = new ClassJar(new File(tempJar));
        ClassProvider provider = new ClassProvider(classpath + File.pathSeparator + tempJar);
        Rebaser rebaser = new Rebaser(temp, provider, _log);
        ClassJarMaker outputMaker = new ClassJarMaker(new File(outputJar));
        try {
            ClassFilter filter = new ClassFilter();
            filter.exclude(exclude);
            _log.info("DummyReWriter classes of : " + temp.getJarFile().getName());
            for (String name : temp.getClasses()) {
                _log.debug("Adding class with no DummyReWriter: " + name);
                outputMaker.add(name, temp);
            }

            rebaser.materialize(outputMaker);
        } finally {
            outputMaker.Close();
        }
    }
}