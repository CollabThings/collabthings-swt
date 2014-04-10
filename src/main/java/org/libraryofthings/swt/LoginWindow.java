package org.libraryofthings.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.libraryofthings.swt.app.LOTApp;

public class LoginWindow {

	protected Shell shell;
	private LOTApp app;

	public LoginWindow(LOTApp app) {
		this.app = app;
	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(459, 235);
		shell.setText("Login");

	}

}
