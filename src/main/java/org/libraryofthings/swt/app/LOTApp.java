package org.libraryofthings.swt.app;

import java.net.MalformedURLException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.model.LOTPart;

import waazdoh.client.WClientListener;
import waazdoh.client.rest.RestServiceClient;
import waazdoh.cp2p.impl.P2PBinarySource;
import waazdoh.cutils.AppPreferences;
import waazdoh.cutils.MLogger;
import waazdoh.cutils.MPreferences;

public class LOTApp {
	private static final String PREFERENCES_PREFIX = "lot";
	private static final String PREFERENCES_USERNAME = "user.name";
	private static final String PREFERENCES_SESSION = "user.session";
	private LOTEnvironment env;
	//
	private MLogger log = MLogger.getLogger(this);
	private AppPreferences preferences;
	private String serviceurl;
	private RestServiceClient service;
	private P2PBinarySource binarysource;

	public LOTApp() throws MalformedURLException {
		preferences = new AppPreferences(LOTApp.PREFERENCES_PREFIX);
		serviceurl = preferences.get(MPreferences.SERVICE_URL, "");
		binarysource = new P2PBinarySource(preferences, true);
		service = new RestServiceClient(serviceurl, binarysource);
	}

	public void addClientListener(WClientListener listener) {
		getEnvironment().getClient().addListener(listener);
	}

	public LOTEnvironment getEnvironment() {
		if (env == null) {
			env = new LOTEnvironment(preferences, binarysource, service);
		}
		return env;
	}

	public void close() {
		getEnvironment().stop();
	}

	public LOTPart newPart() {
		return getEnvironment().getObjectFactory().getPart();
	}

	public boolean loginWithStored() {
		String session = getEnvironment().getPreferences().get(
				LOTApp.PREFERENCES_SESSION, "unknown_session");

		try {
			return getEnvironment().getClient().setSession(session);
		} catch (Exception e) {
			log.info("failed at login " + e);
			return false;
		}
	}

	public boolean isServiceAvailable() {
		return getEnvironment().getClient().getService().isConnected();
	}

	public boolean setSession(String sessionId) {
		boolean b = getEnvironment().getClient().setSession(sessionId);
		if (b) {
			getEnvironment().getPreferences().set(LOTApp.PREFERENCES_SESSION,
					sessionId);
		}
		return b;
	}
}
