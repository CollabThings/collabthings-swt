package org.collabthings.swt.view;

import java.io.File;
import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTPartState;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.SWTResourceManager;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.ObjectViewer;
import org.collabthings.swt.controls.ObjectViewerListener;
import org.collabthings.swt.dialog.LOTMessageDialog;
import org.collabthings.util.LLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.xml.sax.SAXException;

public class PartView extends Composite implements LOTAppControl {
	private static final String DEFAULT_X3D_IMPORTPATH = "lot.gui.default.import_path";
	private LOTPart part;
	private ObjectViewer partobjectviewer;

	private LLog log = LLog.getLogger(this);
	private final LOTApp app;
	private final AppWindow window;
	private ObjectViewer modelobjectviewer;
	private ScrolledComposite sc;
	private Composite c_partproperties;
	private ObjectViewer scadobjectviewer;
	private JFXSimulationComposite scomposite;
	private LOTRunEnvironmentImpl rune;

	public PartView(Composite composite, LOTApp app, AppWindow window, LOTPart p) {
		super(composite, SWT.None);
		this.app = app;
		this.window = window;
		this.part = p;
		init();
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public MenuItem createMenu(Menu menu) {
		return null;
	}

	@Override
	public String getControlName() {
		return "part " + part.getName();
	}

	@Override
	public void selected(AppWindow appWindow) {

	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		Composite c_toolbar = new Composite(this, SWT.NONE);
		c_toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		c_toolbar.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnImport = new Button(c_toolbar, SWT.FLAT);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importSelected();
			}
		});
		btnImport
				.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		btnImport.setText("+");

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

		createDataViewers();

		TabFolder tabFolder = new TabFolder(composite_main, SWT.NONE);

		TabItem tieditor = new TabItem(tabFolder, SWT.NONE);
		tieditor.setText("Editor");

		sc = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		tieditor.setControl(sc);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				updateLayout();
			}
		});

		c_partproperties = new Composite(sc, SWT.NONE);
		c_partproperties.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				true, 1, 1));
		GridLayout gl_c_partproperties = new GridLayout();
		gl_c_partproperties.marginWidth = 0;
		gl_c_partproperties.marginHeight = 0;
		c_partproperties.setLayout(gl_c_partproperties);

		sc.setContent(c_partproperties);
		this.partobjectviewer = new ObjectViewer(app, window, c_partproperties,
				part);

		TabItem titext = new TabItem(tabFolder, SWT.NONE);
		titext.setText("Text");

		YamlEditor yamleditor = new YamlEditor(tabFolder, SWT.NONE, "YAML");
		titext.setControl(yamleditor);
		yamleditor.setObject(this.part);

		GridLayout gridLayout_1 = (GridLayout) partobjectviewer.getLayout();
		gridLayout_1.marginWidth = 0;
		gridLayout_1.marginHeight = 0;
		partobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		partobjectviewer.addListener(new ObjectViewerListener() {
			@Override
			public void valueChanged(String name, Object o) {
				partObjectChanged(name, o);
			}
		});

		Composite c_view = new Composite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);
		//

		scomposite = new JFXSimulationComposite(app, c_view);
		scomposite.setRunEnvironment(getRunEnv());
		composite_main.setWeights(new int[] { 223, 311 });

		new Thread(() -> {
			int hash = 0;
			while (!c_partproperties.isDisposed()) {
				int currenthash = part.getObject().hashCode();
				if (currenthash != hash) {
					hash = currenthash;
					getDisplay().asyncExec(() -> {
						updateViewers();
					});
				}

				synchronized (part) {
					try {
						part.wait(1000);
					} catch (Exception e) {
						log.error(this, "waitpart", e);
					}
				}
			}
		}).start();

		new Thread(() -> {
			long lasttime = System.currentTimeMillis();
			while (!c_partproperties.isDisposed()) {
				long time = System.currentTimeMillis();
				long dtime = time - lasttime;
				lasttime = time;

				scomposite.getView().update();

				synchronized (part) {
					try {
						part.wait(100);
					} catch (Exception e) {
						log.error(this, "waitpart", e);
					}
				}
			}
		}).start();
	}

	private LOTRunEnvironment getRunEnv() {
		LOTClient client = app.getLClient();
		LOTEnvironment e = new LOTEnvironmentImpl(client);
		rune = new LOTRunEnvironmentImpl(client, e);
		LOTPartState state = new LOTPartState(rune, null, part);
		rune.addRunObject("part", state);
		return rune;
	}

	private synchronized void updateViewers() {
		createDataViewers();
	}

	private synchronized void createDataViewers() {
		log.info("Create dataviewers " + part);

		if (c_partproperties != null) {
			Control[] cs = c_partproperties.getChildren();
			for (Control control : cs) {
				control.dispose();
			}

			createPartDataViewer(c_partproperties);
			createModelDataViewer(c_partproperties);

			updateLayout();
		}
	}

	private void updateLayout() {
		int w = sc.getClientArea().width;
		c_partproperties.pack();
		sc.setMinSize(w, c_partproperties.computeSize(w, SWT.DEFAULT).y);
	}

	protected void publish() {
		this.part.publish();
	}

	private void createModelDataViewer(Composite c_partproperties) {
		LOTModel model = part.getModel();
		if (model != null) {
			if (model.getModelType().equals(LOTModel.SCAD)) {
				createOpenSCADViewer(c_partproperties, (LOTOpenSCAD) model);
			} else {
				this.modelobjectviewer = new ObjectViewer(app, window,
						c_partproperties, model);
				modelobjectviewer.setLayoutData(new GridData(SWT.FILL,
						SWT.CENTER, true, false, 1, 1));
				this.modelobjectviewer.addListener(new ObjectViewerListener() {
					@Override
					public void valueChanged(String name, Object o) {
						modelObjectChanged(name, o);
					}
				});
			}
		} else {
			log.info("model null");
		}
	}

	private void createPartDataViewer(Composite c_partproperties) {
	}

	private void createOpenSCADViewer(Composite cparent, LOTOpenSCAD scad) {
		Composite cscad = new Composite(cparent, SWT.NONE);
		cscad.setLayout(new GridLayout(1, false));
		cscad.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lscad = new Label(cscad, SWT.NONE);
		lscad.setText("OpenSCAD");
		lscad.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		lscad.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		lscad.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));

		Composite composite = new Composite(cscad, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		composite.setLayout(new GridLayout(2, false));

		Button bnewscad = new Button(composite, SWT.NONE);
		bnewscad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				part.newSCAD();
			}
		});
		bnewscad.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false,
				1, 1));
		bnewscad.setBounds(0, 0, 75, 25);
		bnewscad.setText("New");

		if (scad != null) {
			Button btnOpen = new Button(composite, SWT.NONE);
			btnOpen.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					window.viewOpenSCAD(scad);
				}
			});
			btnOpen.setText("Open");

			this.scadobjectviewer = new ObjectViewer(app, window, cscad, scad);
			scadobjectviewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					true, false, 1, 1));
			scadobjectviewer.addListener(new ObjectViewerListener() {
				@Override
				public void valueChanged(String name, Object o) {
					partObjectChanged(name, o);
				}
			});

		}
	}

	private void modelObjectChanged(String name, Object value) {

	}

	protected void partObjectChanged(String name, Object o) {
	}

	protected void importSelected() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.x3d" });
		String path = app.getLClient().getPreferences()
				.get(DEFAULT_X3D_IMPORTPATH, "");
		dialog.setFilterPath(path);
		String result = dialog.open();

		try {
			File file = new File(result);
			app.getLClient().getPreferences()
					.set(DEFAULT_X3D_IMPORTPATH, file.getParent());
			importFile(file);
		} catch (SAXException | IOException e) {
			LOTMessageDialog d = new LOTMessageDialog(getShell());
			d.show(e);
		}
	}

	public void importFile(File file) throws SAXException, IOException {
		log.info("loading model " + file);
		part.importModel(file);

		modelobjectviewer.setObject(part.getModel());
	}
}
