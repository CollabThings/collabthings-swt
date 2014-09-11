package org.libraryofthings.swt;

import java.net.MalformedURLException;

import org.libraryofthings.LLog;
import org.libraryofthings.swt.app.LOTApp;

import waazdoh.client.WClient;
import waazdoh.swt.WSWTApp;
import waazdoh.swt.WSWTAppLauncher;
import waazdoh.util.MPreferences;

public final class AppLauncher implements WSWTApp {
	private LOTApp app;
	private LLog log = LLog.getLogger(this);

	@Override
	public void openWindow() {
		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		LLog.getLogger(this).info("Launching " + this);
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
		return app.getEnvironment().getClient();
	}

	@Override
	public MPreferences getPreferences() {
		return app.getEnvironment().getPreferences();
	}

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
	}
}
