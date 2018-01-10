/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
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