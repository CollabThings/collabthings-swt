package org.libraryofthings.swt.app;

import java.net.MalformedURLException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.impl.LOTClientImpl;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTObjectFactory;
import org.libraryofthings.model.LOTPart;

import waazdoh.client.WClientListener;
import waazdoh.client.storage.BeanStorage;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.common.WPreferences;
import waazdoh.common.client.RestServiceClient;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.swt.AppPreferences;

public class LOTApp {
	private static final String PREFERENCES_PREFIX = "lot";
	private LOTClient client;
	//
	private LLog log = LLog.getLogger(this);
	private AppPreferences preferences;
	private String serviceurl;
	private P2PBinarySource binarysource;
	private boolean closed;
	private FileBeanStorage beanstorage;

	public LOTApp() throws MalformedURLException {
		preferences = new AppPreferences(LOTApp.PREFERENCES_PREFIX);
		serviceurl = preferences.get(WPreferences.SERVICE_URL, "");
		beanstorage = new FileBeanStorage(preferences);
		binarysource = new P2PBinarySource(preferences, beanstorage, true);
	}

	public void addClientListener(WClientListener listener) {
		getLClient().getClient().addListener(listener);
	}

	public LOTClient getLClient() {
		if (client == null) {
			client = new LOTClientImpl(preferences, binarysource, beanstorage,
					new RestServiceClient(serviceurl));
		}
		return client;
	}

	public void close() {
		log.info("Closing app");
		getLClient().stop();
		binarysource.close();
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public LOTPart newPart() {
		return getLClient().getObjectFactory().getPart();
	}

	public boolean isServiceAvailable() {
		return getLClient().getClient().isRunning();
	}

	public LOTFactory newFactory() {
		return getLClient().getObjectFactory().getFactory();
	}

	public BeanStorage getBeanStorage() {
		return this.beanstorage;
	}

	public LOTObjectFactory getObjectFactory() {
		return getLClient().getObjectFactory();
	}

}
