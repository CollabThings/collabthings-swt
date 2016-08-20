package org.collabthings.swt;

import java.util.HashMap;
import java.util.Map;

import org.collabthings.model.CTFactory;
import org.collabthings.model.CTPart;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.swt.app.LOTApp;

import waazdoh.common.MStringID;

public class ViewTypes {

	private AppWindow window;

	private Map<String, View> views = new HashMap<>();

	public ViewTypes(AppWindow appWindow, LOTApp app) {
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
		public void view(MStringID id);
	}

	public void view(String type, MStringID id) {
		View view = views.get(type);
		if (view != null) {
			view.view(id);
		} else {
			window.showError("No viewer for type " + type);
		}
	}
}
