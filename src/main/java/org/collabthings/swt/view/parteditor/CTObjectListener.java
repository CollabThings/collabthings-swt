package org.collabthings.swt.view.parteditor;

import org.collabthings.model.CTObject;
import org.collabthings.util.LLog;

public class CTObjectListener {

	private CTObject o;
	private ObjectChanged listener;
	private ContinueRunning dowhile;
	private String hash;

	public CTObjectListener(CTObject ctobject, ContinueRunning dowhile, ObjectChanged changed) {
		this.o = ctobject;
		this.hash = getContentHash();

		this.dowhile = dowhile;
		this.listener = changed;

		new Thread(() -> {
			while (dowhile.doWhile()) {
				if (!hash.equals(getContentHash())) {
					hash = getContentHash();
					changed.chanced();
				}
				doWait();
			}
		}, "ctobjectchangelistener").start();
	}

	private String getContentHash() {
		if (o != null) {
			return o.getObject().getContentHash();
		} else {
			return "unknown";
		}
	}

	private void doWait() {
		synchronized (o) {
			try {
				o.wait(1000);
			} catch (InterruptedException e) {
				LLog.getLogger(this).error(this, "doWait", e);
			}
		}
	}

	public interface ObjectChanged {
		void chanced();
	}

	public interface ContinueRunning {
		boolean doWhile();
	}
}
