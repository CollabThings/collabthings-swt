package org.collabthings.swt.view;

import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.controls.CTText;
import org.eclipse.swt.widgets.Composite;

public class CTControls {

	public static CTText getText(Composite composite, int none) {
		CTText text = new CTText(composite, none);
		
		text.setFont(SWTResourceManager.getDefaultFont());
		return text;
	}

}
