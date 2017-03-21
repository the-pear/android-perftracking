package jp.co.rakuten.sdtd.perf.rewriter.mixins;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import jp.co.rakuten.sdtd.perf.rewriter.Log;

public class MixinMethod {

	public static String PREFIX = "jp_co_rakuten_sdtd_perf_"; 
	
	private final Mixin _mixin;
	private final MethodNode _mn;
	private final Log _log;
	
	public MixinMethod(Mixin mixin, MethodNode mn, Log log) {
		_mixin = mixin;
		_mn = mn;
		_log = log;
	}

	public MethodVisitor rewrite(final String className, ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions) {
		_log.info("Mixing method " + className + "." + name + desc);
		
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		_mn.accept(new MethodVisitor(Opcodes.ASM5, mv) {
			
			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
				if (_mixin.mixinClass.equals(owner)) {
					owner = className;
					if (name.equals(_mn.name)) {
						opcode = Opcodes.INVOKESPECIAL;
						name = PREFIX + name;
					}
				}
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
			
			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String desc) {
				if (_mixin.mixinClass.equals(owner)) {
					for (MixinField f : _mixin.fields) {
						if (f.name.equals(name)) {
							owner = className;
						}
					}
				}
				super.visitFieldInsn(opcode, owner, name, desc);
			}
		});
		
		return cv.visitMethod(Opcodes.ACC_PRIVATE, PREFIX + name, desc, signature, exceptions);
	}
}