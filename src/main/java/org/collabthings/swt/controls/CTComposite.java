package org.collabthings.swt.controls;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;

public class CTComposite extends Composite {

	public CTComposite(Composite shell, int none) {
		super(shell, none);
		setBackground(SWTResourceManager.getControlBg());
		setFont(SWTResourceManager.getDefaultFont());
	}

}
