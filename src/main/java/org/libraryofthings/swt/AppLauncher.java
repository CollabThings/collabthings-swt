package org.libraryofthings.swt;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.swt.app.LOTApp;

public final class AppLauncher {
	private LOTApp app;
	private LoginWindow loginwindow;
	private LLog log = LLog.getLogger(this);
	
	private void openWindow() {
		AppWindow w = new AppWindow(app);
		w.open();
	}

	private void launch() throws MalformedURLException {
		LLog.getLogger(this).info("Launching " + this);
		setProxy();
		//
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
	
	private void setProxy() {
		System.setProperty("java.net.useSystemProxies", "true");
		log.info("detecting proxies");
		List l = null;
		try {
		    l = ProxySelector.getDefault().select(new URI("http://waazdoh.com"));
		} 
		catch (URISyntaxException e) {
		    e.printStackTrace();
		}
		if (l != null) {
		    for (Iterator iter = l.iterator(); iter.hasNext();) {
		    	java.net.Proxy proxy = (java.net.Proxy) iter.next();
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

	public static void main(String[] args) throws MalformedURLException {
		AppLauncher l = new AppLauncher();
		l.launch();
		System.exit(0);
	}
}
