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
package org.collabthings.swt.dialog;

import org.collabthings.CTClient;
import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FindOpenscadDialog {

	public FindOpenscadDialog(CTApp app, AppWindow appWindow, Shell shell) {
		StringBuilder path = new StringBuilder();

		shell.getDisplay().asyncExec(() -> {
			FileDialog fd = new FileDialog(shell);
			fd.setText("path of openscad executable");
			path.append(fd.open());
			app.getLClient().getPreferences().set(CTClient.PREFERENCES_OPENSCADPATH, path.toString());
		});
	}

}
