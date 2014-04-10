package org.libraryofthings.swt.app;

import java.net.MalformedURLException;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.model.LOTPart;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClientListener;
import waazdoh.client.rest.RestClient;
import waazdoh.cp2p.impl.P2PBinarySource;
import waazdoh.cutils.AppPreferences;
import waazdoh.cutils.MLogger;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;

public class LOTApp {
	private static final String PREFERENCES_PREFIX = "lot";
	private static final String PREFERENCES_USERNAME = "user.name";
	private static final String PREFERENCES_SESSION = "user.session";
	private LOTEnvironment env;
	//
	private MLogger log = MLogger.getLogger(this);

	public LOTApp() throws MalformedURLException {
		MPreferences a = new AppPreferences(LOTApp.PREFERENCES_PREFIX);
		MBinarySource b = new P2PBinarySource(a, true);
		CMService c = new RestClient(a.get(MPreferences.SERVICE_URL, ""), b);
		//
		env = new LOTEnvironment(a, b, c);
	}

	public void addClientListener(WClientListener listener) {
		getEnvironment().getClient().addListener(listener);
	}

	public LOTEnvironment getEnvironment() {
		return env;
	}

	public void close() {
		env.stop();
	}

	public LOTPart newPart() {
		return env.getObjectFactory().getPart();
	}

	public boolean loginWithStored() {
		String username = getEnvironment().getPreferences().get(
				LOTApp.PREFERENCES_USERNAME, "unknown_user");
		String session = getEnvironment().getPreferences().get(
				LOTApp.PREFERENCES_SESSION, "unknown_session");

		try {
			return getEnvironment().getClient().setUsernameAndSession(username,
					session);
		} catch (Exception e) {
			log.info("failed at login " + e);
			return false;
		}
	}

	public boolean isServiceAvailable() {
		return getEnvironment().getClient().getService().isConnected();
	}
}
