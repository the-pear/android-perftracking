package jp.co.rakuten.sdtd.perf.rewriter.classes;

public class ClassWriter extends org.objectweb.asm.ClassWriter {
	
	private final ClassProvider _classProvider;

	public ClassWriter(ClassProvider classProvider, int arg0) {
		super(arg0);
		_classProvider = classProvider;
	}
	
	@Override
	protected String getCommonSuperClass(final String type1, final String type2) {
        Class<?> c, d;
        //ClassLoader classLoader = getClass().getClassLoader();
        try {
            //c = Class.forName(type1.replace('/', '.'), false, classLoader);
        	c = _classProvider.getClass(type1.replace('/', '.'));
        	
            //d = Class.forName(type2.replace('/', '.'), false, classLoader);
        	d = _classProvider.getClass(type2.replace('/', '.'));
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }	
}
