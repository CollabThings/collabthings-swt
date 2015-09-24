package org.collabthings.swt.view;

import java.util.Date;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.LOTTask;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.model.run.LOTRunEnvironmentBuilder;
import org.collabthings.simulation.LOTSimpleSimulation;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class RunEnvironmentBuilderView extends Composite implements
		LOTAppControl {
	private LOTRunEnvironmentBuilder builder;

	private LLog log = LLog.getLogger(this);
	private LOTApp app;

	private ScrolledComposite scrolledComposite;
	private Composite composite;
	private AppWindow window;
	private int currentfactoryhash;

	private Text text;
	private PrintOut printout = new PrintOut();

	private int printouthash;

	private Label ltested;

	private MapView mapview;

	public RunEnvironmentBuilderView(Composite composite, LOTApp app,
			AppWindow w, LOTRunEnvironmentBuilder b) {
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
		scrolledComposite
				.setMinSize(w, composite.computeSize(w, SWT.DEFAULT).y);
	}

	private void run() {
		log.info("Launhing simulation " + builder);
		window.viewSimulation(builder);
	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));

		this.scrolledComposite = new ScrolledComposite(composite_main,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);
		c_view.setLayout(new GridLayout(1, false));

		Composite composite_1 = new Composite(c_view, SWT.BORDER);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button btest = new Button(composite_1, SWT.NONE);
		btest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				testRun();
			}
		});
		btest.setText("Test");

		ltested = new Label(composite_1, SWT.NONE);
		ltested.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		ltested.setText("date");

		text = new Text(c_view, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL
				| SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 128;
		text.setLayoutData(gd_text);

		mapview = new MapView(c_view);
		mapview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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
						window.showError(e);
					}
				}
			}
		}).start();
		testRun();
	}

	private void testRun() {
		try {
			LOTRunEnvironment runenv = builder.getRunEnvironment();
			if (runenv != null) {
				runenv.addListener(new RunEnvironmentListener() {

					@Override
					public void taskFailed(LOTRunEnvironment runenv,
							LOTTask task) {
						appendLog("FAILED " + task);
						appendLog("ERROR " + task.getError());
					}

					@Override
					public void event(LOTRuntimeEvent e) {
						appendLog("" + e.getName());
						appendLog("" + e.getObject());
					}
				});

				new Thread(() -> {
					LOTSimpleSimulation s = new LOTSimpleSimulation(runenv);
					s.run(1000);
				}).start();

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

		EnvironmentView ev = new EnvironmentView(composite, window,
				builder.getEnvironment());
		ev.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label l = new Label(composite, SWT.NONE);
		l.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		l.setAlignment(SWT.CENTER);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setText("Created runenvironment");

		try {
			LOTRunEnvironment runEnvironment = this.builder.getRunEnvironment();
			ObjectViewer oview = new ObjectViewer(app, window, composite,
					runEnvironment, new String[] { "info" });
			oview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					1, 1));
		} catch (Exception e) {
			log.error(this, "view", e);
			Label error = new Label(composite, SWT.ERROR);
			error.setText("Loading runenvironment failed");
		}

		updateLayout();
	}

	private synchronized void checkBuilderUpdate() {
		while (!isDisposed()) {
			int nhash = builder.getBean().hashCode();
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

	private synchronized void createDataEditors(Composite c,
			LOTRunEnvironmentBuilder builder2) {
		createFactoryDataViewer(c, builder2);
	}

	private void createFactoryDataViewer(Composite c,
			LOTRunEnvironmentBuilder builder2) {
		GridLayout gl_c_factoryproperties_1 = new GridLayout(1, false);
		gl_c_factoryproperties_1.marginTop = 5;
		gl_c_factoryproperties_1.marginHeight = 0;
		c.setLayout(gl_c_factoryproperties_1);
		ObjectViewer factoryobjectviewer = new ObjectViewer(app, window, c,
				builder2);

		factoryobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 1, 1));
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
