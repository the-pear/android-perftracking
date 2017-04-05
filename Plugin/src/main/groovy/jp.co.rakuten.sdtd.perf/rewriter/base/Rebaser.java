package jp.co.rakuten.sdtd.perf.rewriter.base;

import java.util.HashMap;

import org.gradle.api.logging.Logger;
import org.objectweb.asm.ClassVisitor;

import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJar;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJarMaker;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassProvider;

public class Rebaser {
    private final ClassJar _jar;
    private final HashMap<String, Base> _bases = new HashMap<String, Base>();
    private final ClassProvider _provider;
    private final Logger _log;

    public Rebaser(ClassJar jar, ClassProvider provider, Logger log) {
        _jar = jar;
        _provider = provider;
        _log = log;
    }

    public void add(Base base) {
        _bases.put(base.superName, base);
    }

    public ClassVisitor rewrite(Class<?> clazz, ClassVisitor output) {
        Class<?> s = clazz.getSuperclass();
        if (s != null) {
            if (!_jar.hasClass(s.getName())) {
                while (s != null) {
                    Base base = _bases.get(s.getName());
                    if (base != null) {
                        Materialization m = new Materialization(base, base.materializations.size() + 1, _provider, _log);
                        base.materializations.add(m);
                        return m.rewrite(clazz, output);
                    }
                    s = s.getSuperclass();
                }
            }
        }
        return output;
    }

    public void materialize(ClassJarMaker jarMaker) {
        for (Base base : _bases.values()) {
            for (Materialization m : base.materializations) {
                m.materialize(jarMaker);
            }
        }
    }
}

