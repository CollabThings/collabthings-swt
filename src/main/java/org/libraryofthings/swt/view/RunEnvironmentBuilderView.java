package org.libraryofthings.swt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.model.LOTRunEnvironmentBuilder;
import org.libraryofthings.simulation.LOTSimpleSimulation;
import org.libraryofthings.swt.AppWindow;
import org.libraryofthings.swt.LOTAppControl;
import org.libraryofthings.swt.app.LOTApp;
import org.libraryofthings.swt.controls.ObjectViewer;
import org.eclipse.swt.widgets.Button;

public class RunEnvironmentBuilderView extends Composite implements LOTAppControl {
	private LOTRunEnvironmentBuilder builder;

	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	private Composite cchildrenlist;
	private ScrolledComposite scrolledComposite;
	private Composite composite;
	private AppWindow window;
	private int currentfactoryhash;

	private RunEnvironment4xView eview;

	private LOTSimpleSimulation s;

	public RunEnvironmentBuilderView(Composite composite, LOTApp app, AppWindow w,
			LOTRunEnvironmentBuilder b) {
		super(composite, SWT.None);
		this.app = app;
		this.window = w;
		this.builder = b;
		init();
	}

	private RunEnvironmentBuilderView(Composite c, int i) {
		super(c, i);
		init();
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Factory: " + builder.getName();
	}

	@Override
	public void selected(AppWindow w) {
		updateView();
	}

	private void updateLayout() {
		int w = scrolledComposite.getClientArea().width;
		composite.pack();
		scrolledComposite.setMinSize(w, composite.computeSize(w, SWT.DEFAULT).y);
	}

	private void run() {
		LOTRunEnvironment runEnvironment = builder.getRunEnvironment();
		eview.setRunEnvironment(runEnvironment);
		s = new LOTSimpleSimulation(runEnvironment);
		s.run(60000);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.scrolledComposite = new ScrolledComposite(composite_main, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		this.composite = new Composite(scrolledComposite, SWT.NONE);

		createDataView();

		scrolledComposite.setContent(composite);

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		eview = new RunEnvironment4xView(c_view, SWT.NONE);

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);
		//
		new Thread(() -> checkFactoryUpdate()).start();
	}

	private void createDataView() {
		createDataEditors(composite, builder);

		EnvironmentView ev = new EnvironmentView(composite, window, builder.getEnvironment());
		ev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite cchildren = new Composite(composite, SWT.NONE);
		createChildrenComposite(cchildren);

		updateLayout();
	}

	private void createChildrenComposite(Composite cchildren) {
		cchildren.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_cchildren = new GridLayout(1, false);
		cchildren.setLayout(gl_cchildren);

		Composite cchildrenpanel = new Composite(cchildren, SWT.NONE);
		cchildrenpanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cchildrenpanel.setSize(65, 25);
		GridLayout gl_cchildrenpanel = new GridLayout(2, false);
		cchildrenpanel.setLayout(gl_cchildrenpanel);

		Label lblChildren = new Label(cchildrenpanel, SWT.NONE);
		lblChildren.setText("CHILDREN");
		new Label(cchildrenpanel, SWT.NONE);

	}

	private synchronized void checkFactoryUpdate() {
		while (!isDisposed()) {
			int nhash = builder.getBean().hashCode();
			if (nhash != currentfactoryhash && !window.isSelected(this)) {
				currentfactoryhash = nhash;
				updateView();
			} else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					log.info("" + e);
				}
			}
		}
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			updateDataEditors();
			updateLayout();
		});
	}

	private synchronized void updateDataEditors() {
		updateDataEditors(composite);
	}

	private synchronized void updateDataEditors(Composite c) {
		Control[] cc = c.getChildren();
		for (Control control : cc) {
			control.dispose();
		}
		//
		window.updateObjectMenu(this);

		createDataView();

		updateFactoryHash();
		updateLayout();
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = builder.getBean().hashCode();
	}

	private synchronized void createDataEditors(Composite c, LOTRunEnvironmentBuilder builder2) {
		createFactoryDataViewer(c, builder2);
	}

	private void createFactoryDataViewer(Composite c, LOTRunEnvironmentBuilder builder2) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginTop = 5;
		gl_c_factoryproperties_1.marginHeight = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(c, builder2);

		factoryobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gridLayout = (GridLayout) factoryobjectviewer.getLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
	}

	public MenuItem createMenu(Menu menu) {
		MenuItem mirunenv = new MenuItem(menu, SWT.CASCADE);
		mirunenv.setText("Builder");

		Menu mrunenv = new Menu(mirunenv);
		mirunenv.setMenu(mrunenv);

		MenuItem mifscripts = new MenuItem(mrunenv, SWT.CASCADE);
		mifscripts.setText("Scripts");

		Menu mfmscripts = new Menu(mifscripts);
		mifscripts.setMenu(mfmscripts);

		MenuItem micscripts = new MenuItem(mfmscripts, SWT.CASCADE);
		micscripts.setText("list");

		Menu mscripts = new Menu(micscripts);
		micscripts.setMenu(mscripts);

		MenuItem mifpublish = new MenuItem(mrunenv, SWT.NONE);
		mifpublish.setText("Publish");

		MenuItem mirun = new MenuItem(mrunenv, SWT.NONE);
		mirun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				run();
			}
		});
		mirun.setText("Run");

		mifpublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}

		});

		return mirunenv;
	}

	private void publish() {
		builder.publish();
	}

	private void initLocalMenu(Menu mAddLocalChild) {
	}

}
