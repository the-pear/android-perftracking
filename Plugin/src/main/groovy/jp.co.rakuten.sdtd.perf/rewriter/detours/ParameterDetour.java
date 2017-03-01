package jp.co.rakuten.sdtd.perf.rewriter.detours;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import jp.co.rakuten.sdtd.perf.rewriter.Log;

public class ParameterDetour extends Detour {
	
	private final Log _log;

	public String owner;
	public String detourOwner;
	public String detourName;
	public String detourDesc;
	
	public ParameterDetour(Log log) {
		_log = log;
	}
	
	@Override
	public boolean matchOwner(String owner, Class<?> ownerClass) {
		for (Class<?> c = ownerClass; c != null; c = c.getSuperclass()) {
			if (this.owner.equals(c.getName())) {
				return true;
			}
		}
		return false; 
	}
	
	@Override
	public void rewrite(MethodVisitor mv, int opcode, String owner, Class<?> ownerClass, String name, String desc, boolean itf) {
		_log.info("Detouring " + owner + "." + name + desc);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, detourOwner, detourName, detourDesc, false);
		mv.visitMethodInsn(opcode, owner, name, desc, itf);
	}
}
