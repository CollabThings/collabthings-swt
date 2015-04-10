package org.libraryofthings.swt;

import java.util.HashMap;
import java.util.Map;

import org.libraryofthings.swt.app.LOTApp;

import waazdoh.common.MStringID;

public class ViewTypes {

	private AppWindow window;

	private Map<String, View> views = new HashMap<>();

	public ViewTypes(AppWindow appWindow, LOTApp app) {
		this.window = appWindow;

		views.put("factory", id -> {
			window.viewFactory(app.getObjectFactory().getFactory(id));
		});

	}

	private interface View {
		public void view(MStringID id);
	}

	public void view(String type, MStringID id) {
		views.get(type).view(id);
	}
}
