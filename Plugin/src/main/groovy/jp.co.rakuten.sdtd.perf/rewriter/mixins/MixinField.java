package jp.co.rakuten.sdtd.perf.rewriter.mixins;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.FieldNode;

import jp.co.rakuten.sdtd.perf.rewriter.Log;

public class MixinField {
	
	private final Log _log;
	private final FieldNode _fn;
	public final String name;
	
	public MixinField(Log log, FieldNode fn) {
		_log = log;
		_fn = fn;
		name = fn.name;
	}

	public void add(ClassVisitor cv) {
		_log.info("Adding field " + _fn.name);
		cv.visitField(_fn.access, _fn.name, _fn.desc, _fn.signature, _fn.value);
		//_fn.accept(cv);
	}
}