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
import org.collabthings.core.WClient;
import org.collabthings.core.env.Runner;
import org.collabthings.core.utils.ThreadChecker;
import org.collabthings.core.utils.WPreferences;
import org.collabthings.tk.CTResourceManagerFactory;
import org.collabthings.tk.CTStyleAndResources;
import org.collabthings.util.LLog;

public final class AppLauncher {
	private CTApp app;
	private LLog log = LLog.getLogger(this);

	public void openWindow() {
		setProxy();

		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		log.info("Launching " + this);
		CTResourceManagerFactory.setInstance(new CTStyleAndResources());

		app = new CTApp();
		doLaunch();
	}

	public void doLaunch() {
		try {
			Runner run = new Runner();
			run.run(app.getPreferences());

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
