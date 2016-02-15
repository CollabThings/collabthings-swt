package org.collabthings.swt.app;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.LOTClient;
import org.collabthings.factory.LOTObjectFactory;
import org.collabthings.impl.LOTClientImpl;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTPart;
import org.collabthings.util.LLog;
import org.collabthings.util.LOTTask;

import waazdoh.client.WClientListener;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.common.BeanStorage;
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

	private List<LOTTask> tasks = new LinkedList<LOTTask>();

	public LOTApp() throws MalformedURLException {
		preferences = new AppPreferences(LOTApp.PREFERENCES_PREFIX);
		serviceurl = preferences.get(WPreferences.SERVICE_URL, "");
		beanstorage = new FileBeanStorage(preferences);
		binarysource = new P2PBinarySource(preferences, beanstorage, true);

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runTasks();
				} catch (InterruptedException e) {
					log.error(this, "runTasks", e);
				}
			}
		});
		t.start();
	}

	public void addClientListener(WClientListener listener) {
		getLClient().getClient().addListener(listener);
	}

	public synchronized LOTClient getLClient() {
		if (client == null) {
			client = new LOTClientImpl(preferences, binarysource, beanstorage,
					new RestServiceClient(serviceurl, beanstorage));
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

	private void runTasks() throws InterruptedException {
		synchronized (tasks) {
			while (!isServiceAvailable()) {
				tasks.wait(100);
			}
			while (isClosed()) {
				tasks.wait(100);
			}

			while (!isClosed()) {
				if (tasks.size() > 0) {
					LOTTask task = tasks.remove(0);
					task.run();
				} else {
					tasks.wait(100);
				}
			}
		}
	}

	public void addTask(LOTTask task) {
		synchronized (tasks) {
			tasks.add(task);
		}
	}

}
