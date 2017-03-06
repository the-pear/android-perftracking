package jp.co.rakuten.sdtd.perf.rewriter.detours;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import jp.co.rakuten.sdtd.perf.rewriter.Log;

public class StaticCallDetour extends Detour {
	
	private final Log _log;

	public String owner;
	public String detourOwner;
	
	public StaticCallDetour(Log log) {
		_log = log;
	}
	
	@Override
	public boolean matchOwner(String owner, Class<?> ownerClass) {
		return this.owner.equals(owner);
	}
	
	@Override
	public void rewrite(MethodVisitor mv, int opcode, String owner, Class<?> ownerClass, String name, String desc, boolean itf) {
		_log.info("Detouring " + owner + "." + name + desc);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, detourOwner, name, desc, false);
	}
}
