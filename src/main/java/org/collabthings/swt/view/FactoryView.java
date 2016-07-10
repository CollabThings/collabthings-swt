package org.collabthings.swt.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTScript;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.swt.AppWindow;
import org.collabthings.swt.LOTAppControl;
import org.collabthings.swt.LOTSWT;
import org.collabthings.swt.app.LOTApp;
import org.collabthings.swt.controls.CTButton;
import org.collabthings.swt.controls.CTComposite;
import org.collabthings.swt.controls.CTTabFolder;
import org.collabthings.swt.controls.LocalObjectsMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;

public class FactoryView extends CTComposite implements LOTAppControl, ScriptUser {
	private CTFactory factory;
	private LOTApp app;

	private AppWindow window;

	private int currentfactoryhash;
	private YamlEditor yamleditor;
	private YamlEditor enveditor;

	public FactoryView(Composite composite, LOTApp app, AppWindow w, CTFactory f) {
		super(composite, SWT.None);
		this.app = app;
		this.window = w;
		this.factory = f;
		init();
	}

	private FactoryView(Composite c, int i) {
		super(c, i);
		init();
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public String getControlName() {
		return "Factory: " + factory.getName();
	}

	@Override
	public void addScript(CTScript script) {
		factory.addScript("script" + factory.getScripts().size() + " " + script.getName(), script);
	}

	private synchronized void updateFactoryHash() {
		currentfactoryhash = factory.getObject().hashCode();
	}

	@Override
	public void selected(AppWindow w) {
		updateView();
	}

	private void viewChild(CTFactory f) {
		window.viewFactory(f);
	}

	CTScript addScript(String name) {
		return factory.addScript(name);
	}

	private void updateLayout() {

	}

	private void init() {
		GridLayout gridLayout = new GridLayout(1, false);
		LOTSWT.setDefaults(gridLayout);
		setLayout(gridLayout);

		SashForm composite_main = new SashForm(this, SWT.NONE);
		composite_main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new CTComposite(composite_main, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_composite);
		composite.setLayout(gl_composite);

		Composite cpanel = new CTComposite(composite, SWT.NONE);
		GridLayout gl_cpanel = new GridLayout(1, false);
		LOTSWT.setDefaults(gl_cpanel);
		cpanel.setLayout(gl_cpanel);
		cpanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		CTButton btnAddChild = new CTButton(cpanel, SWT.NONE);
		btnAddChild.addSelectionListener(() -> {
			addChild();
		});
		btnAddChild.setText("add child");

		CTTabFolder tabFolder = new CTTabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		yamleditor = new YamlEditor(tabFolder, SWT.NONE, "Factory");
		tabFolder.addTab("main", yamleditor, null);

		yamleditor.setObject(this.factory);

		enveditor = new YamlEditor(tabFolder, SWT.NONE, "Environment");
		tabFolder.addTab("env", enveditor, null);
		
		enveditor.setObject(factory.getEnvironment());

		Composite c_view = new CTComposite(composite_main, SWT.NONE);
		c_view.setLayout(new FillLayout(SWT.HORIZONTAL));
		c_view.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c_view.setBounds(0, 0, 64, 64);

		composite_main.setWeights(new int[] { 384, 421 });

		Menu tempmenu = new Menu(this);
		setMenu(tempmenu);
		createMenu(tempmenu);

		new Thread(() -> checkFactoryUpdate()).start();
	}

	private void updateFactory() {
		new Thread(() -> {
			CTClient client = app.getLClient();
			CTEnvironment env = new CTEnvironmentImpl(client);
			CTRunEnvironment runenv = new CTFactoryState(client, env, "view", factory).getRunEnvironment();
			// view.doRepaint();
		}).start();
	}

	private synchronized void updateView() {
		getDisplay().asyncExec(() -> {
			updateDataEditors();
		});
	}

	private synchronized void updateDataEditors() {
		window.updateObjectMenu(this);
		updateFactory();
		updateLayout();
	}

	protected void environmentObjectChanged(String name, Object o) {
		updateFactory();
	}

	public MenuItem createMenu(Menu menu) {
		MenuItem mifactory = new MenuItem(menu, SWT.CASCADE);
		mifactory.setText("Factory");

		Menu mfactory = new Menu(mifactory);
		mifactory.setMenu(mfactory);

		MenuItem mifscripts = new MenuItem(mfactory, SWT.CASCADE);
		mifscripts.setText("Scripts");

		Menu mfmscripts = new Menu(mifscripts);
		mifscripts.setMenu(mfmscripts);

		MenuItem mfsaddnew = new MenuItem(mfmscripts, SWT.NONE);
		mfsaddnew.setText("New");
		mfsaddnew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addScript("script" + factory.getEnvironment().getScripts().size());
			}
		});

		MenuItem micscripts = new MenuItem(mfmscripts, SWT.CASCADE);
		micscripts.setText("list");

		Menu mscripts = new Menu(micscripts);
		micscripts.setMenu(mscripts);

		Set<String> scripts = factory.getEnvironment().getScripts();
		for (String string : scripts) {
			MenuItem mscript = new MenuItem(mscripts, SWT.NONE);
			mscript.setText(string);
			mscript.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					scriptMenuSelected(string);
				};
			});
		}

		MenuItem mfmImport = new MenuItem(mfmscripts, SWT.NONE);
		mfmImport.setText("Import");
		mfmImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				importSelected();
			}
		});

		MenuItem mifpublish = new MenuItem(mfactory, SWT.NONE);
		mifpublish.setText("Publish");

		MenuItem mntmAddChild = new MenuItem(mfactory, SWT.CASCADE);
		mntmAddChild.setText("Add child");

		Menu mAddChild = new Menu(mntmAddChild);
		mntmAddChild.setMenu(mAddChild);

		MenuItem mifaddnewchild = new MenuItem(mAddChild, SWT.NONE);
		mifaddnewchild.setText("New");

		MenuItem miAddLocalChild = new MenuItem(mAddChild, SWT.CASCADE);
		miAddLocalChild.setText("Local");

		Menu mAddLocalChild = new Menu(miAddLocalChild);
		miAddLocalChild.setMenu(mAddLocalChild);
		initLocalMenu(mAddLocalChild);

		mifaddnewchild.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addChild();

			}
		});

		mifpublish.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				publish();
			}
		});

		return mifactory;
	}

	private void publish() {
		factory.publish();
	}

	private void initLocalMenu(Menu mAddLocalChild) {
		LocalObjectsMenu m = new LocalObjectsMenu(window, mAddLocalChild);
		m.addObjectHandler(CTFactoryImpl.BEANNAME, (data) -> {
			CTFactory f = window.getApp().getLClient().getObjectFactory().getFactory(data.getIDValue("id"));
			addChild(f);
		});
	}

	private void addChild() {
		this.factory.addFactory();
	}

	private void addChild(CTFactory f) {
		this.factory.addFactory("factory" + this.factory.getFactories().size(), f);
	}

	private synchronized void checkFactoryUpdate() {
		while (!isDisposed()) {
			int nhash = factory.getObject().hashCode();
			if (nhash != currentfactoryhash) {
				yamleditor.setObject(factory);
				enveditor.setObject(factory.getEnvironment());
				currentfactoryhash = nhash;
				updateView();
			} else {
				try {
					wait(100);
				} catch (InterruptedException e) {
					window.showError(e);
				}
			}
		}
	}

	protected void importSelected() {
		getDisplay().asyncExec(() -> {
			try {
				FileDialog fd = new FileDialog(getShell(), SWT.MULTI);
				fd.open();

				String fpath = fd.getFilterPath();
				String[] fns = fd.getFileNames();
				for (String string : fns) {
					CTScript s = addScript(string);
					byte[] bs = Files.readAllBytes(Paths.get(fpath + File.separator + string));
					s.setScript(new String(bs));
				}
			} catch (IOException e) {
				window.showError(e);
			}
		});
	}

	protected void scriptMenuSelected(String string) {
		window.viewScript(factory.getScript(string));
	}
}
