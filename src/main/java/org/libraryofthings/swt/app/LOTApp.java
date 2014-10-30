package org.libraryofthings.swt.app;

import java.net.MalformedURLException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.impl.LOTClientImpl;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTPart;

import waazdoh.client.WClientListener;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.service.rest.RestServiceClient;
import waazdoh.util.AppPreferences;
import waazdoh.util.MPreferences;

public class LOTApp {
	private static final String PREFERENCES_PREFIX = "lot";
	private LOTClient client;
	//
	private LLog log = LLog.getLogger(this);
	private AppPreferences preferences;
	private String serviceurl;
	private RestServiceClient service;
	private P2PBinarySource binarysource;
	private boolean closed;

	public LOTApp() throws MalformedURLException {
		preferences = new AppPreferences(LOTApp.PREFERENCES_PREFIX);
		serviceurl = preferences.get(MPreferences.SERVICE_URL, "");
		binarysource = new P2PBinarySource(preferences, true);
		service = new RestServiceClient(serviceurl, binarysource);
	}

	public void addClientListener(WClientListener listener) {
		getEnvironment().getClient().addListener(listener);
	}

	public LOTClient getEnvironment() {
		if (client == null) {
			client = new LOTClientImpl(preferences, binarysource, service);
		}
		return client;
	}

	public void close() {
		getEnvironment().stop();
		binarysource.close();
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public LOTPart newPart() {
		return getEnvironment().getObjectFactory().getPart();
	}

	public boolean isServiceAvailable() {
		return getEnvironment().getClient().getService().isConnected();
	}

	public LOTFactory newFactory() {
		return getEnvironment().getObjectFactory().getFactory();
	}
}
