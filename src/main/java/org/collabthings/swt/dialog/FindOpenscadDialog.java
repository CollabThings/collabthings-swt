package org.collabthings.swt.dialog;

import org.collabthings.CTClient;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FindOpenscadDialog {

	public FindOpenscadDialog(LOTApp app, AppWindow appWindow, Shell shell) {
		FileDialog fd = new FileDialog(shell);
		fd.setText("path of openscad executable");
		String path = fd.open();
		app.getLClient().getPreferences().set(CTClient.PREFERENCES_OPENSCADPATH, path);
	}

}
