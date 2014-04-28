package org.libraryofthings.swt;

import java.net.MalformedURLException;

import org.libraryofthings.swt.app.LOTApp;

public final class AppLauncher {
	private LOTApp app;
	private LoginWindow loginwindow;

	private void openWindow() {
		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		app = new LOTApp();
		try {
			loginwindow = new LoginWindow(app);
			loginwindow.open();

			if (!app.getEnvironment().getClient().isRunning()) {
				app.close();
			} else {
				openWindow();
			}
		} finally {
			app.close();
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
	}
}
