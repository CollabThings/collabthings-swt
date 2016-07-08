package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

public class CTTabFolder extends CTabFolder {

	public CTTabFolder(Composite composite, int flat) {
		super(composite, flat);
		setBackground(SWTResourceManager.getControlBg());
		setFont(SWTResourceManager.getDefaultFont());
	}

}
