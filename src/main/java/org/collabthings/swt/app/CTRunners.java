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
package org.collabthings.swt.app;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.app.CTApp;
import org.collabthings.swt.AppWindow;
import org.collabthings.util.LLog;

public class CTRunners {

	private AppWindow window;
	private CTApp app;

	@SuppressWarnings("rawtypes")
	private List<CTRunner> runners = new ArrayList<>();
	private String status;

	@SuppressWarnings("rawtypes")
	public CTRunners(AppWindow appWindow, CTApp app) {
		this.window = appWindow;
		this.app = app;

		new Thread(() -> {
			while (!app.isClosed()) {

				List<CTRunner> orgrunners = runners;
				runners = new ArrayList<>();
				orgrunners.stream().forEach(r -> {
					setStatus("Running count:" + orgrunners.size() + " " + r.getName());
					if (r.check(window)) {
						runners.add(r);
					}
				});

				setStatus("Done");

				synchronized (this.app) {
					try {
						this.app.wait(100);
					} catch (InterruptedException e) {
						LLog.getLogger(this).error(this, "runrunners", e);
					}
				}
			}
		}).start();
	}

	private void setStatus(String status) {
		window.setStatus(AppWindow.STATUS_RUNNERS, status);
	}

	@SuppressWarnings("rawtypes")
	public CTRunner add(CTRunner runner) {
		runners.add(runner);
		return runner;
	}

}
