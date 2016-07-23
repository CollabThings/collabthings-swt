package org.collabthings.swt.view;

import java.util.Date;

import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.simulation.CTSimpleSimulation;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTLabel;
import org.collabthings.swt.controls.CTTabFolder;
import org.collabthings.swt.controls.CTText;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

public class RunEnvironmentBuilderView extends CTComposite implements LOTAppControl {
	private CTRunEnvironmentBuilder builder;

	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	private Composite composite;
	private AppWindow window;
	private int currentfactoryhash;

	private CTText text;
	private PrintOut printout = new PrintOut();

	private int printouthash;

	private CTLabel ltested;

	private MapView mapview;

	private YamlEditor maineditor;

	private YamlEditor enveditor;

	public RunEnvironmentBuilderView(Composite composite, LOTApp app, AppWindow w, CTRunEnvironmentBuilder b) {
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

	private void run() {
		log.info("Launhing simulation " + builder);
		window.viewSimulation(builder);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		CTTabFolder tabFolder = new CTTabFolder(composite_main, SWT.BORDER);

		// main yaml editor

		this.maineditor = new YamlEditor(tabFolder, SWT.NONE, "main");
		tabFolder.addTab("Main", maineditor, null);
		;
		// environment yaml editor
		this.enveditor = new YamlEditor(tabFolder, SWT.NONE, "env");
		tabFolder.addTab("Env", enveditor, null);

		this.composite = new CTComposite(tabFolder, SWT.NONE);
		tabFolder.addTab("Builder", composite, null);

		Composite c_view = new CTComposite(composite_main, SWT.NONE);
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);
		c_view.setLayout(new GridLayout(1, false));

		Composite composite_1 = new CTComposite(c_view, SWT.BORDER);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTButton btest = new CTButton(composite_1, SWT.NONE);
		btest.addSelectionListener(() -> {
			testRun();
		});
		btest.setText("Test");

		ltested = new CTLabel(composite_1, SWT.NONE);
		ltested.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		ltested.setText("date");

		text = new CTText(c_view, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 128;
		text.setLayoutData(gd_text);

		mapview = new MapView(c_view);
		mapview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_main.setWeights(new int[] { 1, 1 });

		getDisplay().asyncExec(() -> {
			try {
				mapview.set(this.builder.getRunEnvironment());
			} catch (Exception e) {
				log.error(this, "mapview", e);
			}
		});

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);
		//
		new Thread(() -> checkBuilderUpdate()).start();
		new Thread(() -> {
			while (!isDisposed()) {
				if (printouthash != printout.toText().hashCode()) {
					getDisplay().asyncExec(() -> {
						text.setText(printout.toText());
					});
				}

				synchronized (this) {
					try {
						this.wait(300);
					} catch (Exception e) {
						window.showError("Interrupted", e);
					}
				}
			}
		}).start();
		testRun();
	}

	private void testRun() {
		try {
			CTRunEnvironment runenv = builder.getRunEnvironment();
			if (runenv != null) {
				runenv.addListener(new RunEnvironmentListener() {

					@Override
					public void taskFailed(CTRunEnvironment runenv, CTEnvironmentTask task) {
						appendLog("FAILED " + task);
						appendLog("ERROR " + task.getError());
					}

					@Override
					public void event(CTRuntimeEvent e) {
						appendLog("" + e.getName());
						appendLog("" + e.getObject());
					}
				});

				new Thread(() -> {
					CTSimpleSimulation s = new CTSimpleSimulation(runenv);
					s.run(1000);
				}, "Test run " + this).start();

				ltested.setText("" + new Date());
			} else {
				appendLog("RunEnvironment null");
			}
		} catch (Exception e) {
			appendLog("Exception " + e);
			log.error(this, "testRun", e);
		}
	}

	protected void appendLog(String string) {
		printout.append(string);
	}

	private void createDataView() {
		createDataEditors(composite, builder);

		EnvironmentView ev = new EnvironmentView(composite, window, builder.getEnvironment());
		ev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTLabel label = new CTLabel(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTLabel l = new CTLabel(composite, SWT.NONE);
		l.setAlignment(SWT.CENTER);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setText("Created runenvironment");

		try {
			CTRunEnvironment runEnvironment = this.builder.getRunEnvironment();
			ObjectViewer oview = new ObjectViewer(app, window, composite, new String[] { "info" });
			oview.setObject(runEnvironment.getEnvironment());
			oview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		} catch (Exception e) {
			log.error(this, "view", e);
			CTLabel error = new CTLabel(composite, SWT.ERROR);
			error.setText("Loading runenvironment failed");
		}
	}

	private synchronized void checkBuilderUpdate() {
		while (!isDisposed()) {
			int nhash = builder.getObject().hashCode();
			if (nhash != currentfactoryhash && window.isSelected(this)) {
				currentfactoryhash = nhash;
				updateView();
			} else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					log.error(this, "checkBuilderUpdate", e);
				}
			}
		}
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			updateDataEditors();
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
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = builder.getObject().hashCode();
	}

	private synchronized void createDataEditors(Composite c, CTRunEnvironmentBuilder builder2) {
		createFactoryDataViewer(c, builder2);
	}

	private void createFactoryDataViewer(Composite c, CTRunEnvironmentBuilder builder2) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginTop = 5;
		gl_c_factoryproperties_1.marginHeight = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(app, window, c);
		factoryobjectviewer.setObject(builder2);

		factoryobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gridLayout = (GridLayout) factoryobjectviewer.getLayout();
		LOTSWT.setDefaults(gridLayout);
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
		mirun.setText("Run " + builder);

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

}
