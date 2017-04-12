package org.collabthings.swt;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.collabthings.app.CTApp;
import org.collabthings.swt.app.LoginWindow;
import org.collabthings.util.LLog;

import waazdoh.client.WClient;
import waazdoh.client.utils.ThreadChecker;
import waazdoh.common.WPreferences;

public final class AppLauncher {
	private CTApp app;
	private LLog log = LLog.getLogger(this);

	private LoginWindow loginwindow;

	public void openWindow() {
		setProxy();

		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		log.info("Launching " + this);
		app = new CTApp();
		doLaunch();
	}

	public void doLaunch() {
		try {
			loginwindow = new LoginWindow(app);
			loginwindow.open();

			new ThreadChecker(() -> getClient().isRunning());

			if (!getClient().isRunning()) {
				close();
			} else {
				openWindow();
			}
		} finally {
			close();
		}
	}

	private void setProxy() {
		System.setProperty("java.net.useSystemProxies", "true");
		log.info("detecting proxies");
		List<Proxy> l = null;
		try {
			l = ProxySelector.getDefault().select(new URI("http://waazdoh.com"));
		} catch (URISyntaxException e) {
			log.error(this, "setProxy", e);
		}

		if (l != null) {
			for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {
				Proxy proxy = iter.next();
				log.info("proxy hostname : " + proxy.type());

				InetSocketAddress addr = (InetSocketAddress) proxy.address();

				if (addr == null) {
					log.info("No Proxy");
				} else {
					log.info("proxy hostname : " + addr.getHostName());
					System.setProperty("http.proxyHost", addr.getHostName());
					log.info("proxy port : " + addr.getPort());
					System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
				}
			}
		}
	}

	public void close() {
		app.close();
	}

	public WClient getClient() {
		return app.getLClient().getClient();
	}

	public WPreferences getPreferences() {
		return app.getLClient().getPreferences();
	}

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
	}
}
