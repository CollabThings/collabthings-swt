package org.libraryofthings.swt;

import java.net.MalformedURLException;

import org.libraryofthings.LLog;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.client.WClient;
import waazdoh.common.WPreferences;
import waazdoh.swt.WSWTApp;
import waazdoh.swt.WSWTAppLauncher;

public final class AppLauncher implements WSWTApp {
	private LOTApp app;
	private LLog log = LLog.getLogger(this);

	@Override
	public void openWindow() {
		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		log.info("Launching " + this);
		WSWTAppLauncher l = new WSWTAppLauncher();
		app = new LOTApp();
		l.launch(this);
	}

	@Override
	public void close() {
		app.close();
	}

	@Override
	public WClient getClient() {
		return app.getLClient().getClient();
	}

	@Override
	public WPreferences getPreferences() {
		return app.getLClient().getPreferences();
	}

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
		System.exit(0);
	}
}
