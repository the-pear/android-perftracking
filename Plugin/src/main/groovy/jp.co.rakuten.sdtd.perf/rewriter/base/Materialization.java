package jp.co.rakuten.sdtd.perf.rewriter.base;

import org.gradle.api.logging.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJarMaker;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassProvider;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassWriter;

public class Materialization {
    public final Base base;
    public final int index;
    public final String name;
    public final String internalName;
    public String internalSuperName;

    private final ClassProvider _provider;
    private final Logger _log;

    public Materialization(Base base, int index, ClassProvider provider, Logger log)
    {
        this.base = base;
        this.index = index;
        name = base.name + "_" + index;
        internalName = base.internalName + "_" + index;
        _provider = provider;
        _log = log;
    }

    public ClassVisitor rewrite(final Class<?> clazz, final ClassVisitor output) {
        _log.debug("Rebasing " + clazz.getName() + " to " + name);

        return new ClassVisitor(Opcodes.ASM5, output) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                internalSuperName = superName;
                super.visit(version, access, name, signature, internalName, interfaces);
            }
        };
    }

    public void materialize(ClassJarMaker jarMaker) {
        _log.debug("Materializing " + name);

        base.cn.name = internalName;
        base.cn.superName = internalSuperName;
        ClassWriter cw = new ClassWriter(_provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        base.cn.accept(cw);
        jarMaker.add(name, cw.toByteArray());
    }
}

