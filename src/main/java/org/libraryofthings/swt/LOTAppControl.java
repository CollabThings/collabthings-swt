package org.libraryofthings.swt;

import org.eclipse.swt.widgets.Menu;

public interface LOTAppControl {

	void selected(AppWindow appWindow);

	Menu createMenu(Menu menu);

}
