package org.collabthings.swt;

import java.util.HashMap;
import java.util.Map;

import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.run.LOTRunEnvironmentBuilder;
import org.collabthings.swt.app.LOTApp;

import waazdoh.common.MStringID;

public class ViewTypes {

	private AppWindow window;

	private Map<String, View> views = new HashMap<>();

	public ViewTypes(AppWindow appWindow, LOTApp app) {
		this.window = appWindow;
		app.getObjectFactory().addInfoListener(appWindow);

		views.put("factory", id -> {
			LOTFactory factory = app.getObjectFactory().getFactory(id);
			if (factory != null) {
				window.viewFactory(factory);
			} else {
				window.showError("Failed to get factory " + id);
			}
		});

		views.put("builder", id -> {
			LOTRunEnvironmentBuilder b = app.getObjectFactory()
					.getRuntimeBuilder(id);
			if (b != null) {
				window.viewRuntimeBuilder(b);
			} else {
				window.showError("Failed to get envbuilder " + id);
			}
		});

		views.put("partbuilder", id -> {
			LOTPartBuilder b = app.getObjectFactory().getPartBuilder(id);
			if (b != null) {
				window.viewPartBuilder(b);
			} else {
				window.showError("Failed to get partbuilder " + id);
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
