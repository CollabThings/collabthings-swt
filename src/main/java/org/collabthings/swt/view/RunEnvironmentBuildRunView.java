package org.collabthings.swt.view;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.model.LOTRunEnvironmentBuilder;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.app.LOTApp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

public class RunEnvironmentBuildRunView extends Composite implements
		LOTAppControl {

	private LOTRunEnvironmentBuilder builder;
	private RunEnvironment4xView eview;
	private LOTSimpleSimulation s;
	private AppWindow window;
	private LOTApp app;
	private Text text;

	public RunEnvironmentBuildRunView(Composite parent, LOTApp app,
			AppWindow appWindow, LOTRunEnvironmentBuilder builder) {
		super(parent, SWT.NONE);
		this.builder = builder;
		this.app = app;
		this.window = appWindow;
		setLayout(new GridLayout(1, false));

		LOTRunEnvironment runEnvironment = builder.getRunEnvironment();

		SashForm sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite c_view = new Composite(sashForm, SWT.NONE);
		c_view.setLayout(new GridLayout(1, false));
		eview = new RunEnvironment4xView(c_view, SWT.NONE);
		eview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		eview.setRunEnvironment(runEnvironment);
		sashForm.setWeights(new int[] {143, 294});

		new Thread(() -> {
			s = new LOTSimpleSimulation(runEnvironment);
			s.run(60000);
		}).start();

		eview.runWhile(() -> {
			return !s.isDone();
		});
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
