package jp.co.rakuten.sdtd.perf.rewriter.mixins;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import jp.co.rakuten.sdtd.perf.rewriter.Log;

public class MixinLoader {
	
	private final Log _log;
	
	public MixinLoader(Log log) {
		_log = log;
	}
	
	public Mixin loadMixin(ClassNode cn) {
		Mixin mixin = new Mixin(_log);
		mixin.mixinClass = cn.name; 
		
		AnnotationNode a;
		
		a = getAnnotation(cn, "Ljp/co/rakuten/sdtd/perf/runtime/annotations/MixClass;");
		if (a != null) {
			mixin.targetClass= ((Type)a.values.get(1)).getClassName();
		}
		
		a = getAnnotation(cn, "Ljp/co/rakuten/sdtd/perf/runtime/annotations/MixSubclassOf;");
		if (a != null) {
			mixin.targetSubclassOf = ((Type)a.values.get(1)).getClassName();
		}
		
		a = getAnnotation(cn, "Ljp/co/rakuten/sdtd/perf/runtime/annotations/MixImplementationOf;");
		if (a != null) {
			mixin.targetImplementationOf = ((Type)a.values.get(1)).getClassName();
		}

		a = getAnnotation(cn, "Ljp/co/rakuten/sdtd/perf/runtime/annotations/ChangeBaseTo;");
		if (a != null) {
			mixin.changeBaseTo = ((Type)a.values.get(1)).getClassName().replace('.', '/');
			
			if (mixin.targetSubclassOf != null) {
				mixin.changeBaseFrom = mixin.targetSubclassOf.replace('.', '/');
			}
		}
		
		for (Object o : cn.methods) {
			MethodNode mn = (MethodNode)o;
			a = getAnnotation(mn);
			if (a != null) {
				if (a.desc.equals("Ljp/co/rakuten/sdtd/perf/runtime/annotations/ReplaceMethod;")) {
					mixin.methods.put(mn.name + mn.desc, new MixinMethod(mixin, mn, _log));
				}
			}
		}
		
		for (Object o : cn.fields) {
			FieldNode fn = (FieldNode)o;
			a = getAnnotation(fn);
			if (a != null) {
				if (a.desc.equals("Ljp/co/rakuten/sdtd/perf/runtime/annotations/AddField;")) {
					mixin.fields.add(new MixinField(_log, fn));
				}
			}
		}
		
		return mixin;
	}
	
	private AnnotationNode getAnnotation(ClassNode cn, String desc) {
		AnnotationNode a;
		if (cn.visibleAnnotations != null) {
			for (int i = 0; i < cn.visibleAnnotations.size(); i++) {
				a = (AnnotationNode)cn.visibleAnnotations.get(i);
				if (desc.equals(a.desc)) {
					return a;
				}
			}
		}
		if (cn.invisibleAnnotations != null) {
			for (int i = 0; i < cn.invisibleAnnotations.size(); i++) {
				a = (AnnotationNode)cn.invisibleAnnotations.get(i);
				if (desc.equals(a.desc)) {
					return a;
				}
			}
		}
		return null;
	}
	
	private AnnotationNode getAnnotation(MethodNode mn) {
		if ((mn.visibleAnnotations != null) && (mn.visibleAnnotations.size() > 0)) {
			return (AnnotationNode)mn.visibleAnnotations.get(0);
		}
		if ((mn.invisibleAnnotations != null) && (mn.invisibleAnnotations.size() > 0)) {
			return (AnnotationNode)mn.invisibleAnnotations.get(0);
		}
		return null;
	}
	
	private AnnotationNode getAnnotation(FieldNode fn) {
		if ((fn.visibleAnnotations != null) && (fn.visibleAnnotations.size() > 0)) {
			return (AnnotationNode)fn.visibleAnnotations.get(0);
		}
		if ((fn.invisibleAnnotations != null) && (fn.invisibleAnnotations.size() > 0)) {
			return (AnnotationNode)fn.invisibleAnnotations.get(0);
		}
		return null;
	}
}
