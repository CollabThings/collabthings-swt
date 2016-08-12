package org.collabthings.swt;

import org.collabthings.model.CTObject;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public interface CTAppControl {

	void selected(AppWindow appWindow);

	MenuItem createMenu(Menu menu);

	String getControlName();

	Control getControl();

	CTObject getObject();

}
