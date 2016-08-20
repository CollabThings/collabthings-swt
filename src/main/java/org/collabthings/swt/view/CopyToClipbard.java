package org.collabthings.swt.view;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class CopyToClipbard {
	public Composite c;
	public String id;

	public CopyToClipbard(Composite c, String id2) {
		this.c = c;
		this.id = id2;
		Clipboard cb = new Clipboard(c.getDisplay());
		cb.setContents(new String[] { id }, new Transfer[] { TextTransfer.getInstance() });
	}

}