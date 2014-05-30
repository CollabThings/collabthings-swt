package org.libraryofthings.swt.app;

import java.net.MalformedURLException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.model.LOTPart;

import waazdoh.client.WClient;
import waazdoh.client.WClientListener;
import waazdoh.client.rest.RestServiceClient;
import waazdoh.cp2p.impl.P2PBinarySource;
import waazdoh.cutils.AppPreferences;
import waazdoh.cutils.MPreferences;
import waazdoh.swt.WSWTApp;

public class LOTApp {
	private static final String PREFERENCES_PREFIX = "lot";
	private LOTEnvironment env;
	//
	private LLog log = LLog.getLogger(this);
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

	public boolean isServiceAvailable() {
		return getEnvironment().getClient().getService().isConnected();
	}
}
