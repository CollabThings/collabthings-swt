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

import java.util.HashMap;
import java.util.Map;

import org.collabthings.app.CTApp;
import org.collabthings.datamodel.WStringID;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTPart;
import org.collabthings.model.run.CTRunEnvironmentBuilder;

public class ViewTypes {

	private AppWindow window;

	private Map<String, View> views = new HashMap<>();

	public ViewTypes(AppWindow appWindow, CTApp app) {
		this.window = appWindow;
		app.getObjectFactory().addInfoListener(appWindow);

		views.put("factory", id -> {
			CTFactory factory = app.getObjectFactory().getFactory(id);
			if (factory != null) {
				window.viewFactory(factory);
			} else {
				window.showError("Failed to get factory " + id);
			}
		});

		views.put("builder", id -> {
			CTRunEnvironmentBuilder b = app.getObjectFactory().getRuntimeBuilder(id);
			if (b != null) {
				window.viewRunEnvironmentBuilder(b);
			} else {
				window.showError("Failed to get envbuilder " + id);
			}
		});

		views.put("part", id -> {
			CTPart b = app.getObjectFactory().getPart(id);
			if (b != null) {
				window.getMainView().viewPart(b);
			} else {
				window.showError("Failed to get part " + id);
			}
		});

		views.put("runenvbuilder", id -> {
			CTRunEnvironmentBuilder b = app.getObjectFactory().getRuntimeBuilder(id);
			if (b != null) {
				window.viewRunEnvironmentBuilder(b);
			} else {
				window.showError("Failed to get part " + id);
			}
		});
	}

	private interface View {
		public void view(WStringID id);
	}

	public void view(String type, WStringID id) {
		View view = views.get(type);
		if (view != null) {
			view.view(id);
		} else {
			window.showError("No viewer for type " + type);
		}
	}
}
