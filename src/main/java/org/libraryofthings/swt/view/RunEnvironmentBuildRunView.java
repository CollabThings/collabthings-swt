package org.libraryofthings.swt.view;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.model.LOTRunEnvironmentBuilder;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;

public class RunEnvironmentBuildRunView extends Composite implements LOTAppControl {

	private LOTRunEnvironmentBuilder builder;
	private RunEnvironment4xView eview;
	private LOTSimpleSimulation s;
	private AppWindow window;
	private LOTApp app;

	public RunEnvironmentBuildRunView(Composite parent, LOTApp app, AppWindow appWindow,
			LOTRunEnvironmentBuilder builder) {
		super(parent, SWT.NONE);
		this.builder = builder;
		this.app = app;
		this.window = appWindow;
		setLayout(new GridLayout(1, false));

		LOTRunEnvironment runEnvironment = builder.getRunEnvironment();

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(sashForm, SWT.NONE);

		Composite c_view = new Composite(sashForm, SWT.NONE);
		eview = new RunEnvironment4xView(c_view, SWT.NONE);
		eview.setRunEnvironment(runEnvironment);
		sashForm.setWeights(new int[] { 1, 1 });

		new Thread(() -> {
			s = new LOTSimpleSimulation(runEnvironment);
			s.run(60000);
		}).start();

	}

	@Override
	public MenuItem createMenu(Menu menu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Run";
	}

	@Override
	public void selected(AppWindow appWindow) {

	}
}
