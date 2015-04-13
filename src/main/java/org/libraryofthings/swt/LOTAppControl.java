package org.libraryofthings.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.libraryofthings.model.LOTObject;
import org.libraryofthings.model.LOTScript;

public interface LOTAppControl {

	void selected(AppWindow appWindow);

	MenuItem createMenu(Menu menu);

	String getControlName();

	Control getControl();

}
