package jp.co.rakuten.sdtd.perf.rewriter.base;

import java.util.ArrayList;

import org.objectweb.asm.tree.ClassNode;

public class Base {
    public String name;
    public String internalName;
    public String superName;
    public ClassNode cn;
    public final ArrayList<Materialization> materializations = new ArrayList<Materialization>();
}
