package org.libraryofthings.swt;

public final class AppLauncher {

	public static void main(String[] args) {
		AppWindow w = new AppWindow();
		w.launch();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
