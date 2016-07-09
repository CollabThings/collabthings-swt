package org.collabthings.swt.view;

import org.collabthings.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CTControls {

	public static Text getText(Composite composite, int none) {
		Text text = new Text(composite, none);
		text.setFont(SWTResourceManager.getDefaultFont());
		return text;
	}

}
