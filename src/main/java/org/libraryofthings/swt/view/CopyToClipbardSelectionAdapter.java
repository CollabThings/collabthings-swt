package org.libraryofthings.swt.view;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class CopyToClipbardSelectionAdapter extends SelectionAdapter {
	public Composite c;
	public String id;

	public CopyToClipbardSelectionAdapter(Composite c, String id2) {
		this.c = c;
		this.id = id2;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		Clipboard cb = new Clipboard(c.getDisplay());
		cb.setContents(new String[] { id }, new Transfer[] { TextTransfer.getInstance() });
	}

}