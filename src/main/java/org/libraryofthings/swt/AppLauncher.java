package org.libraryofthings.swt;

import java.net.MalformedURLException;

import org.libraryofthings.swt.app.LOTApp;

import waazdoh.client.WClientListener;

public final class AppLauncher {
	private LOTApp app;

	private void openWindow() {
		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		app = new LOTApp();
		app.addClientListener(new WClientListener() {
			@Override
			public void loggedIn() {
				openWindow();
			}

		});

		if (app.isServiceAvailable()) {
			if (!app.loginWithStored()) {
				LoginWindow loginwindow = new LoginWindow(app);
				loginwindow.open();
				if (!app.getEnvironment().getClient().isRunning()) {
					app.close();
				}
			}
		} else {
			openWindow();
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
	}
}
