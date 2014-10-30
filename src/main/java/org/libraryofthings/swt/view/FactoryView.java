package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.swt.SWTResourceManager;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.libraryofthings.swt.controls.ObjectViewerListener;

import waazdoh.util.ConditionWaiter;

public class FactoryView extends Composite {
	private LOTFactory factory;
	private RunEnvironment4xView view;
	private LLog log = LLog.getLogger(this);
	private LOTApp app;
	private ObjectViewer factoryobjectviewer;
	private ObjectViewer envobjectviewer;

	public FactoryView(LOTApp app, LOTFactory f, Composite composite) {
		super(composite, SWT.None);
		this.app = app;
		this.factory = f;
		init();
	}

	private FactoryView(Composite c, int i) {
		super(c, i);
		init();
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		c_toolbar.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button button = new Button(c_toolbar, SWT.FLAT);
		button.setText("A");
		button.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));

		Button btnPublish = new Button(c_toolbar, SWT.NONE);
		btnPublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}
		});
		btnPublish.setText("Publish");

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		Composite c_partproperties = new Composite(composite_main, SWT.NONE);
		c_partproperties.setLayout(new FillLayout(SWT.VERTICAL));
		c_partproperties.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				true, 1, 1));
		c_partproperties.setBounds(0, 0, 64, 64);

		createFactoryDataViewer(c_partproperties);
		createEnvironmentDataViewer(c_partproperties);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		view = new RunEnvironment4xView(c_view, SWT.NONE);

		updateFactory();

		composite_main.setWeights(new int[] { 136, 311 });
	}

	private void updateFactory() {
		LOTClient client = app.getEnvironment();
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironment runenv = new LOTFactoryState(client, env, "view",
				factory).getRunEnvironment();
		view.setRunEnvironment(runenv);
		view.step(0);
		view.doRepaint();
	}

	protected void publish() {
		this.factory.publish();
	}

	private void createEnvironmentDataViewer(Composite c_partproperties) {
		this.envobjectviewer = new ObjectViewer(c_partproperties,
				factory.getEnvironment());
		this.envobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				environmentObjectChanged(name, o);
			}
		});
	}

	private void createFactoryDataViewer(Composite c_partproperties) {
		this.factoryobjectviewer = new ObjectViewer(c_partproperties, factory);
		factoryobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				factoryObjectChanged(name, o);
			}
		});
	}

	protected void factoryObjectChanged(String name, Object o) {
		updateFactory();
	}

	protected void environmentObjectChanged(String name, Object o) {
		updateFactory();
	}

}
